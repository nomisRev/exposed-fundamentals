package org.jetbrains.exposed.example

import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager
import kotlin.time.Clock
import kotlin.uuid.Uuid

/** A custom SQL function from lesson 4. PostgreSQL needs its `unaccent` extension installed first. */
fun <T : String?> Expression<T>.unaccent() =
  CustomStringFunction("unaccent", this)

/** The window expression from lesson 4, kept as an Exposed expression rather than raw query text. */
private object TotalCount : ExpressionWithColumnType<Long>() {
  override val columnType = LongColumnType()

  override fun toQueryBuilder(queryBuilder: QueryBuilder) {
    queryBuilder.append("COUNT(*) OVER ()")
  }
}

// lesson 2 — "JDBC ~ R2DBC". The JDBC version is deliberately used in this JDBC-only file.
private fun JdbcTransaction.lesson2Talks(limit: Int = 10, offset: Long = 30): Query =
  Talks.select(Talks.speakerId, Talks.title, Talks.description, Talks.startsAt)
    .where { Talks.description like "%Kotlin%" }
    .orderBy(Talks.title, SortOrder.ASC)
    .limit(limit)
    .offset(offset)

// lesson 2 — the query-only and ResultRow-access snippets share this SQL shape.
private fun JdbcTransaction.lesson2Previews(): Query =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(
      Talks.id,
      Talks.title,
      Talks.description,
      Talks.startsAt,
      ProfileTable.name,
      ProfileTable.avatarUrl,
    )
    .where { Talks.isPublished eq true }

// lesson 2 — batchInsert. NewTalk includes all required fields from the real Talks table.
private fun JdbcTransaction.lesson2InsertBatchReturning(talks: List<NewTalk>): List<ResultRow> =
  Talks.batchInsert(talks) { talk ->
    this[Talks.speakerId] = talk.speakerId
    this[Talks.hostId] = talk.hostId
    this[Talks.title] = talk.title
    this[Talks.slug] = talk.slug
    this[Talks.description] = talk.description
    this[Talks.startsAt] = talk.startsAt
  }

private fun JdbcTransaction.lesson2InsertBatch(talks: List<NewTalk>) {
  Talks.batchInsert(talks, shouldReturnGeneratedValues = false) { talk ->
    this[Talks.speakerId] = talk.speakerId
    this[Talks.hostId] = talk.hostId
    this[Talks.title] = talk.title
    this[Talks.slug] = talk.slug
    this[Talks.description] = talk.description
    this[Talks.startsAt] = talk.startsAt
  }
}

// lesson 2 — update. Talks has isPublished rather than the illustrative TalkStatus column.
private fun JdbcTransaction.lesson2Update(talkId: Uuid) {
  Talks.update({ Talks.id eq talkId }) {
    it[Talks.isPublished] = true
  }
}

// lesson 2 — updateReturning. Materialising the iterable is what sends the statement.
private fun JdbcTransaction.lesson2UpdateReturning(talkId: Uuid): List<ResultRow> =
  Talks.updateReturning(
    returning = listOf(Talks.speakerId, Talks.title, Talks.startsAt),
    where = { Talks.id eq talkId },
  ) {
    it[Talks.isPublished] = true
  }.toList()

// lesson 2 — upsert variants. The real Talks table uses the unique slug as its conflict key.
private fun JdbcTransaction.lesson2Upsert(talk: NewTalk) {
  Talks.upsert(Talks.slug, onUpdateExclude = [Talks.id], onUpdate = {

  }) {
    it[Talks.slug] = talk.slug
    it[Talks.speakerId] = talk.speakerId
    it[Talks.hostId] = talk.hostId
    it[Talks.title] = talk.title
    it[Talks.description] = talk.description
    it[Talks.startsAt] = talk.startsAt
  }
}

private fun JdbcTransaction.lesson2UpsertReturning(talk: NewTalk): List<ResultRow> =
  Talks.upsertReturning(
    keys = arrayOf(Talks.slug),
    returning = listOf(Talks.id, Talks.updatedAt),
    // onUpdate replaces the default SET list, so restate the columns a conflict should take from EXCLUDED.
    onUpdate = {
      it[Talks.title] = insertValue(Talks.title)
      it[Talks.description] = insertValue(Talks.description)
      it[Talks.startsAt] = insertValue(Talks.startsAt)
      it[Talks.updatedAt] = CurrentTimestamp
    },
  ) {
    it[Talks.slug] = talk.slug
    it[Talks.speakerId] = talk.speakerId
    it[Talks.hostId] = talk.hostId
    it[Talks.title] = talk.title
    it[Talks.description] = talk.description
    it[Talks.startsAt] = talk.startsAt
  }.toList()

// lesson 2 — deleteWhere and deleteReturning are separate statements, so log each independently.
private fun JdbcTransaction.lesson2Delete(talkId: Uuid): Int =
  Talks.deleteWhere { Talks.id eq talkId }

private fun JdbcTransaction.lesson2DeleteReturning(talkId: Uuid): List<ResultRow> =
  Talks.deleteReturning { Talks.id eq talkId }.toList()

// lesson 4 — aggregate / HAVING snippets.
private fun JdbcTransaction.lesson4BookmarkCounts(minimum: Long? = null): Query {
  val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")
  return Talks.leftJoin(Bookmarks)
    .select(Talks.title, bookmarkCount)
    .groupBy(Talks.id, Talks.title)
    .apply { minimum?.let { having { Bookmarks.talkId.count() greaterEq it } } }
    .orderBy(bookmarkCount, SortOrder.DESC)
}

// lesson 4 — subquery and correlated EXISTS snippets.
private fun JdbcTransaction.lesson4TalkIdsForTag(tag: String): Query =
  TalkTags.innerJoin(Tags)
    .select(TalkTags.talkId)
    .where { Tags.label eq tag }

private fun JdbcTransaction.lesson4TalksWithTag(tag: String): Query =
  Talks.select(Talks.title)
    .where { Talks.id inSubQuery lesson4TalkIdsForTag(tag) }

private fun JdbcTransaction.lesson4TalksWhereTagExists(tag: String): Query {
  fun hasTag() = TalkTags.innerJoin(Tags)
    .select(TalkTags.talkId)
    .where { TalkTags.talkId eq Talks.id and (Tags.label eq tag) }

  return Talks.select(Talks.title).where { exists(hasTag()) }
}

// lesson 4 — trim/lowerCase, custom unaccent, and a window expression need a SELECT to emit SQL.
private fun JdbcTransaction.lesson4NormalizedNamesAndTitles(): Query {
  val normalizedName = ProfileTable.name.trim().lowerCase().alias("normalized_name")
  val normalizedTitle = Talks.title.lowerCase().unaccent().alias("normalized_title")

  return Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(normalizedName, normalizedTitle)
    .orderBy(normalizedTitle, SortOrder.ASC)
}

private fun JdbcTransaction.lesson4TalksWithTotalCount(): Query =
  Talks.select(Talks.id, Talks.title, TotalCount)
    .where { Talks.isPublished eq true }

private fun logSqlSection(name: String) {
  println("\n${"=".repeat(88)}\nSQL CHECK: $name\n${"-".repeat(88)}")
}

private fun JdbcTransaction.insertTalk(
  speakerId: Uuid,
  hostId: Uuid,
  title: String,
  suffix: String,
): Uuid {
  val now = Clock.System.now()
  return Talks.insert {
    it[Talks.speakerId] = speakerId
    it[Talks.hostId] = hostId
    it[Talks.title] = title
    it[Talks.slug] = "$suffix-${Uuid.random()}"
    it[Talks.description] = "A Kotlin talk used to reveal generated SQL."
    it[Talks.startsAt] = now
  }.let { (it get Talks.id).value }
}

class MyPostgreSQLContainer : PostgreSQLContainer<MyPostgreSQLContainer>("postgres:18-alpine") {
  override fun configure() {
    addEnv("POSTGRES_DB", databaseName)
    addEnv("POSTGRES_USER", username)
    addEnv("POSTGRES_PASSWORD", password)
  }
}

fun main() {
  MyPostgreSQLContainer()
    .withCommand("postgres", "-c", "log_statement=all")

    .apply { start() }
    .use { container ->
      val jdbcUrl = "${container.jdbcUrl}?reWriteBatchedInserts=true"
      val database = Database.connect(
        jdbcUrl,
        container.driverClassName,
        container.username,
        container.password,
      )
      transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ProfileTable, Talks, Tags, TalkTags, Bookmarks)
        exec("CREATE EXTENSION IF NOT EXISTS unaccent")

        val speaker = ProfileTable.insert {
          it[name] = "  Ada Lovelace  "
          it[email] = "ada@example.test"
        }.let { (it get ProfileTable.id).value }
        val host = ProfileTable.insert {
          it[name] = "Grace Hopper"
          it[email] = "grace@example.test"
        }.let { (it get ProfileTable.id).value }
        val talkId = insertTalk(speaker, host, "Kotlin fundamentals", "fundamentals")
        val tagId = Tags.insert {
          it[label] = "kotlin"
        }.let { (it get Tags.id).value }
        TalkTags.insert {
          it[TalkTags.talkId] = talkId
          it[TalkTags.tagId] = tagId
        }

        logSqlSection("Lesson 2 — filtered, ordered, paginated talks")
        lesson2Talks().toList()
        logSqlSection("Lesson 2 — published talk previews")
        lesson2Previews().toList()
        logSqlSection("Lesson 2 — batch insert returning generated values")
        lesson2InsertBatchReturning(
          listOf(
            NewTalk(speaker, host, "Kotlin batch returning one", "First batch row", Clock.System.now()),
            NewTalk(speaker, host, "Kotlin batch returning two", "Second batch row", Clock.System.now()),
          ),
        )
        logSqlSection("Lesson 2 — batch insert without generated values")
        lesson2InsertBatch(
          listOf(
            NewTalk(speaker, host, "Kotlin batch one", "First rewritten batch row", Clock.System.now()),
            NewTalk(speaker, host, "Kotlin batch two", "Second rewritten batch row", Clock.System.now()),
          ),
        )
        logSqlSection("Lesson 2 — update")
        lesson2Update(talkId)
        logSqlSection("Lesson 2 — update returning")
        lesson2UpdateReturning(talkId)

        val upsertTalk = NewTalk(speaker, host, "Kotlin upsert", "Upsert example", Clock.System.now())
        logSqlSection("Lesson 2 — upsert")
        lesson2Upsert(upsertTalk)
        logSqlSection("Lesson 2 — upsert returning")
        lesson2UpsertReturning(upsertTalk)

        logSqlSection("Lesson 4 — bookmark counts")
        lesson4BookmarkCounts().toList()
        logSqlSection("Lesson 4 — bookmark counts with HAVING")
        lesson4BookmarkCounts(minimum = 10L).toList()
        logSqlSection("Lesson 4 — tag IDs subquery")
        lesson4TalkIdsForTag("kotlin").toList()
        logSqlSection("Lesson 4 — talks filtered with IN subquery")
        lesson4TalksWithTag("kotlin").toList()
        logSqlSection("Lesson 4 — talks filtered with correlated EXISTS")
        lesson4TalksWhereTagExists("kotlin").toList()
        logSqlSection("Lesson 4 — normalized names and titles")
        lesson4NormalizedNamesAndTitles().toList()
        logSqlSection("Lesson 4 — total count window expression")
        lesson4TalksWithTotalCount().toList()

        val deleteId = insertTalk(speaker, host, "Delete count", "delete-count")
        logSqlSection("Lesson 2 — delete where")
        lesson2Delete(deleteId)
        val deleteReturningId = insertTalk(speaker, host, "Delete returning", "delete-returning")
        logSqlSection("Lesson 2 — delete returning")
        lesson2DeleteReturning(deleteReturningId)
        exec("CREATE TABLE jdbc_batch_probe (value TEXT NOT NULL)")
      }

      // Control: submit the same two-row pattern directly through JDBC's batch API.
      DriverManager.getConnection(jdbcUrl, container.username, container.password).use { connection ->
        connection.prepareStatement("INSERT INTO jdbc_batch_probe (value) VALUES (?)").use { insert ->
          listOf("plain JDBC batch one", "plain JDBC batch two").forEach { value ->
            insert.setString(1, value)
            insert.addBatch()
          }
          insert.executeBatch()
        }
      }

      println("\n${"=".repeat(88)}\nPOSTGRESQL SERVER LOG (log_statement=all)\n${"-".repeat(88)}")
      println(container.logs)
    }
}
