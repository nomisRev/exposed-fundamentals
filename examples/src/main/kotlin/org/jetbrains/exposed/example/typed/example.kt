package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object UserTable : LongIdTable() {
  val username = varchar("username", 255)
  val email = varchar("email", 255)

  val imageUrl = varchar("image_url", 255)
}

data class User(val id: Long, val username: String, val email: String)

data class UserWithImage(
  val id: Long,
  val username: String,
  val email: String,
  val imageUrl: String
)

fun main() {
  UserTable
    .select(UserTable.id, UserTable.username, UserTable.email)
    .map(::User)

  UserTable
    .select(UserTable.id, UserTable.username, UserTable.email)
    .select(UserTable.imageUrl)
    .map(::UserWithImage)

  UserTable.select(UserTable.id)
    .select(UserTable.username, UserTable.email)
    .map(::User)

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
}
