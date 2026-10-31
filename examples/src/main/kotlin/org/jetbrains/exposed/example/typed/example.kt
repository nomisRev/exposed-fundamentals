package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.MyPostgreSQLContainer
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.use

object UserTable : LongIdTable() {
  val username = varchar("username", 255)
  val email = varchar("email", 255)

  val imageUrl = varchar("image_url", 255).nullable()
}

data class User(val id: Long, val username: String, val email: String)

data class UserWithImage(
  val id: Long,
  val username: String,
  val email: String,
  val imageUrl: String?
)

// TODO support where, grouping, etc
fun main() {
  MyPostgreSQLContainer()
    .withCommand("postgres", "-c", "log_statement=all")
    .apply { start() }
    .use { container ->
      transaction(
        Database.connect(
          container.jdbcUrl,
          container.driverClassName,
          container.username,
          container.password,
        )
      ) {
        SchemaUtils.create(UserTable)

        /**
         * For insertReturning I choose to allow `select` after `insertReturning`,
         * this allows for tracking the types which doesn't work for `returning: List<Expression<*>> = columns`.
         */
        UserTable.insertReturning {
          it[username] = "Simon"
          it[email] = "sv@jb.com"
        }
          .select(UserTable.id, UserTable.username, UserTable.email)
          .map(::User)
          .also(::println)

        UserTable
          .select(UserTable.id, UserTable.username, UserTable.email)
          .map(::User)
          .also(::println)

        UserTable
          .select(UserTable.id, UserTable.username, UserTable.email)
          .select(UserTable.imageUrl)
          .map(::UserWithImage)
          .also(::println)

        UserTable.select(UserTable.id)
          .select(UserTable.username, UserTable.email)
          .map(::User)
          .also(::println)
      }
    }
}
