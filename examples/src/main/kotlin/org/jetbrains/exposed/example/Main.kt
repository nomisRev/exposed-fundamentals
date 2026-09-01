package org.jetbrains.exposed.example

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.example.jdbc.talkIdsForTag
import org.jetbrains.exposed.v1.core.CustomStringFunction
import org.jetbrains.exposed.v1.core.ExpressionWithColumnType
import org.jetbrains.exposed.v1.core.LongColumnType
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.andIfNotNull
import org.jetbrains.exposed.v1.core.compoundAnd
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.exists
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.inSubQuery
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.leftJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.orIfNotNull
import org.jetbrains.exposed.v1.core.trim
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.testcontainers.containers.PostgreSQLContainer
import kotlin.uuid.Uuid

private const val POSTGRES_IMAGE = "postgres:18.6-alpine"

/**
 * Starts an ephemeral PostgreSQL database, creates the example schema, and executes the query below.
 *
 * Run with: ./gradlew :examples:run
 */
fun main(): Unit = runBlocking {
  PostgreSQLContainer<Nothing>(POSTGRES_IMAGE).use { postgres ->
    postgres.start()

    val database = Database.connect(
      url = "jdbc:postgresql://${postgres.host}:${postgres.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT)}/${postgres.databaseName}",
      user = postgres.username,
      password = postgres.password,
    )

    transaction(database) {
      addLogger(StdOutSqlLogger)
      SchemaUtils.create(ProfileTable, Talks, Tags, TalkTags, Bookmarks)

//      val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")
//      Talks.leftJoin(Bookmarks)
//        .select(Talks.title, bookmarkCount)
//        .groupBy(Talks.id, Talks.title)
//        .having { Bookmarks.talkId.count() greaterEq 10 }
//        .orderBy(bookmarkCount, SortOrder.DESC)
//        .toList()
//        .forEach { row -> println("${row[Talks.title]}: ${row[bookmarkCount]} bookmarks") }

      fun talkIdsForTag(tag: String) =
        TalkTags.innerJoin(Tags)
          .select(TalkTags.talkId)
          .where { Tags.label eq tag }

      Talks.select(Talks.title)
        .where { Talks.id inSubQuery talkIdsForTag("kotlin") }
//        .singleOrNull()

      fun hasTag(tag: String) =
        TalkTags.innerJoin(Tags)
          .select(TalkTags.talkId)
          .where { TalkTags.talkId eq Talks.id and (Tags.label eq tag) }

      Talks.select(Talks.title)
        .where { exists(hasTag("kotlin")) }
//        .singleOrNull()

      val tag: String? = "kotlin"
      val speakerName: String? = null
      val published: Boolean? = null


      Op.TRUE
        .andIfNotNull(tag?.let { Talks.id inSubQuery talkIdsForTag(it) })
        .andIfNotNull(speakerName?.let { ProfileTable.name eq it })
        .andIfNotNull(published?.let { Talks.isPublished eq it })

//      Talks.innerJoin(ProfileTable)
//        .select(Talks.columns + ProfileTable.columns)
//        .where { predicates.compoundAnd() }
//        .limit(size).offset(offset)
//
//      CustomStringFunction(
//        ""
//      )
//      exec("CREATE EXTENSION IF NOT EXISTS unaccent;")

//      normalizedTalks().toList()
      publishedTalkPage().toList()
    }
  }
}

fun JdbcTransaction.searchTalks(tag: String?, title: String?): Query {
  val query = Talks.selectAll()
  tag?.let { query.andWhere { Talks.id inSubQuery talkIdsForTag(it) } }
  title?.let { query.andWhere { Talks.title like "%$it%" } }
  return query
}

data class NormalizedTalk(
  val normalizedName: String,
  val normalizedTitle: String?,
)

/**
 * Uses built-in string functions for the profile name and PostgreSQL's
 * unaccent function for the talk title.
 *
 * PostgreSQL requires:
 * CREATE EXTENSION IF NOT EXISTS unaccent;
 */
fun normalizedTalks(): List<NormalizedTalk> = transaction {
  val normalizedName =
    ProfileTable.name.trim().lowerCase().alias("normalized_name")

  val normalizedTitle =
    CustomStringFunction("unaccent", Talks.title.lowerCase()).alias("normalized_title")

  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(normalizedName, normalizedTitle)
    .orderBy(normalizedTitle to SortOrder.ASC)
    .map { row ->
      NormalizedTalk(
        normalizedName = row[normalizedName],
        normalizedTitle = row[normalizedTitle],
      )
    }
}

private object TotalCount : ExpressionWithColumnType<Long>() {
  override val columnType = LongColumnType()

  override fun toQueryBuilder(queryBuilder: QueryBuilder) {
    queryBuilder.append("COUNT(*) OVER ()")
  }
}

data class TalkPageRow(
  val id: Uuid,
  val title: String,
  val totalCount: Long,
)

fun publishedTalkPage(): List<TalkPageRow> = transaction {
  Talks.select(Talks.id, Talks.title, TotalCount)
    .where { Talks.isPublished eq true }
    .orderBy(Talks.startsAt to SortOrder.ASC)
    .map { row ->
      TalkPageRow(
        id = row[Talks.id].value,
        title = row[Talks.title],
        totalCount = row[TotalCount],
      )
    }
}