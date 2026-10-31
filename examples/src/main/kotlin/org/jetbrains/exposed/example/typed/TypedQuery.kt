@file:Suppress("unused")

package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.jdbc.Query

sealed interface ResultSpec {

  data object Empty : ResultSpec

  @JvmInline
  value class Extract<A>(val extract: (ResultRow) -> A) : ResultSpec

  class And<A : ResultSpec, B : ResultSpec>(val a: A, val b: B) : ResultSpec
}

sealed interface TypedResult<out A : ResultSpec> {
  val columns: List<Column<*>>
  val result: (columns: List<Column<*>>) -> Iterable<ResultRow>
}

class TypedQuery<out A : ResultSpec>(
  override val columns: List<Column<*>>,
  override val result: (columns: List<Column<*>>) -> Query
) : TypedResult<A>

class TypedExecutable<out A : ResultSpec>(
  override val columns: List<Column<*>>,
  override val result: (columns: List<Column<*>>) -> Iterable<ResultRow>
) : TypedResult<A>
