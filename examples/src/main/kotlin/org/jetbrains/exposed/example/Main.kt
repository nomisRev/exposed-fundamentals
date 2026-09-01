package org.jetbrains.exposed.example

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.leftJoin
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import org.testcontainers.containers.PostgreSQLContainer

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

      val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")
      Talks.leftJoin(Bookmarks)
        .select(Talks.title, bookmarkCount)
        .groupBy(Talks.id, Talks.title)
        .orderBy(bookmarkCount, SortOrder.DESC)
        .toList()
        .forEach { row -> println("${row[Talks.title]}: ${row[bookmarkCount]} bookmarks") }

    }
  }
}
