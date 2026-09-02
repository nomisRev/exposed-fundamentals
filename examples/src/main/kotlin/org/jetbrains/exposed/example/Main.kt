package org.jetbrains.exposed.example

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.CustomStringFunction
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.exists
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

fun <T : String?>  Expression<T>.unaccent() =
  CustomStringFunction("unaccent", this.lowerCase())

fun main() {
  Talks.title.lowerCase().unaccent().alias("lower")

  PostgreSQLContainer("postgres:13.2")
    .apply { start() }
    .use {
      val db = Database.connect(it.jdbcUrl, it.driverClassName, it.username, it.password)
      transaction(db) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Talks, Tags, TalkTags)

        fun hasTag(tag: String) =
          TalkTags.innerJoin(Tags)
            .select(TalkTags.talkId)
            .where { TalkTags.talkId eq Talks.id and (Tags.label eq tag) }

        Talks.select(Talks.title)
          .where { exists(hasTag("kotlin")) }
          .singleOrNull()
      }
    }
}