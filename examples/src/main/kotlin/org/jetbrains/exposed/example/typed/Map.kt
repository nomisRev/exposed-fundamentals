package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import kotlin.collections.map

internal fun <A> ResultRow.getValue(column: Column<*>): A {
  val a: Any? = this[column]
  val value = if (a is EntityID<*>) a.value else a
  return value as A
}

fun <A, B : Extract<A>, C> TypedResult<B>.map(block: (A) -> C): List<C> {
  require(columns.size == 1) { "Exactly one column is required" }
  return result(this.columns)
    .map { block(it.getValue(columns.single())) }
}

fun <A, B, C> TypedResult<And<Extract<A>, Extract<B>>>.map(block: (A, B) -> C): List<C> {
  require(columns.size == 2) { "Exactly two columns are required" }
  return result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1])
      )
    }
}

fun <A, B, C, D> TypedResult<And<And<Extract<A>, Extract<B>>, Extract<C>>>.map(block: (A, B, C) -> D): List<D> {
  require(columns.size == 3) { "Exactly two columns are required" }
  return result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2])
      )
    }
}

fun <A, B, C, D, E> TypedResult<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>>.map(block: (A, B, C, D) -> E): List<E> {
  require(columns.size == 3) { "Exactly two columns are required" }
  return result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3])
      )
    }
}
