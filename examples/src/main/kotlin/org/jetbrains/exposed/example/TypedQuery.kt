@file:Suppress("unused")

package org.jetbrains.exposed.example

import org.jetbrains.exposed.example.ResultSpec.And
import org.jetbrains.exposed.example.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.select as ExposedSelect
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.select

object UserTable : LongIdTable() {
  val username = varchar("username", 255)
  val email = varchar("email", 255)
}

sealed interface ResultSpec {

  @JvmInline
  value class Extract<A>(val extract: (ResultRow) -> A) : ResultSpec

  class And<A : ResultSpec, B : ResultSpec>(val a: A, val b: B) : ResultSpec
}

fun <A> Table.select(column: Column<A>): TypedQuery<Extract<A>> =
  TypedQuery(ExposedSelect(column), Extract { it[column] })

fun <A : Any, ID : EntityID<A>> Table.select(column: Column<ID>): TypedQuery<Extract<A>> =
  TypedQuery(ExposedSelect(column), Extract { it[column].value })

fun <A : ResultSpec, B : ResultSpec> Table.select(
  columnA: Column<A>,
  columnB: Column<B>
): TypedQuery<And<Extract<A>, Extract<B>>> =
  TypedQuery(
    ExposedSelect(columnA, columnB),
    And(Extract { it[columnA] }, Extract { it[columnB] })
  )

fun <A : Any, ID : EntityID<A>, C, D> Table.select(
  columnA: Column<ID>,
  columnB: Column<C>,
  columnC: Column<D>
): TypedQuery<And<And<Extract<A>, Extract<C>>, Extract<D>>> =
  TypedQuery(
    ExposedSelect(columnA, columnB, columnC),
    And(
      And(
        Extract { it[columnA].value },
        Extract { it[columnB] }
      ),
      Extract { it[columnC] },
    )
  )

fun <A, B, C> Table.select(
  columnA: Column<A>,
  columnB: Column<B>,
  columnC: Column<C>
): TypedQuery<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedQuery(
    ExposedSelect(columnA, columnB, columnC),
    And(
      And(
        Extract { it[columnA] },
        Extract { it[columnB] }
      ),
      Extract { it[columnC] },
    )
  )

class TypedQuery<A : ResultSpec>(val query: Query, internal val spec: A)

fun <A, B : Extract<A>, C> TypedQuery<B>.map(block: (A) -> C): List<C> =
  query.map { row -> block(spec.extract(row)) }

fun <A, B, C> TypedQuery<And<Extract<A>, Extract<B>>>.map(block: (A, B) -> C): List<C> =
  query.map {
    val x = spec
    val a = x.a.extract(it)
    val b = x.b.extract(it)
    block(a, b)
  }

fun <A, B, C, D> TypedQuery<And<And<Extract<A>, Extract<B>>, Extract<C>>>.map(block: (A, B, C) -> D): List<D> =
  query.map {
    val x = spec
    val a = x.a.a.extract(it)
    val b = x.a.b.extract(it)
    val c = x.b.extract(it)
    block(a, b, c)
  }

fun <A : ResultSpec, B> TypedQuery<A>.select(columnB: Column<B>): TypedQuery<And<A, Extract<B>>> =
  TypedQuery(
    query.adjustSelect {
      select(columns + columnB)
    },
    And(
      spec,
      Extract { it[columnB] },
    )
  )

fun <A : ResultSpec, B, C> TypedQuery<A>.select(
  columnB: Column<B>,
  columnC: Column<C>
): TypedQuery<And<And<A, Extract<B>>, Extract<C>>> =
  TypedQuery(
    query.adjustSelect {
      select(columns + columnB)
    },
    And(
      And(
        spec,
        Extract { it[columnB] },
      ),
      Extract { it[columnC] }
    )
  )

data class User(val id: Long, val username: String, val email: String)

fun main() {
  UserTable
    .select(UserTable.id, UserTable.username, UserTable.email)
    .map(::User)

  UserTable.select(UserTable.id)
    .select(UserTable.username, UserTable.email)
    .map(::User)
}
