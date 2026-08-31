package org.jetbrains.example.test

import org.jetbrains.example.test.Test.name
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.jdbc.select

object Test : LongIdTable() {
  val name = varchar("name", 200)
  val avatarUrl = varchar("avatarUrl", 200)
}

fun example() {
  Test.select(Test.id, name, Test.avatarUrl)
}

fun Test.selectExample() =
  select(id, name, avatarUrl)
