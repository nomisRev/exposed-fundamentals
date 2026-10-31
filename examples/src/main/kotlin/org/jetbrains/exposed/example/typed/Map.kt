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

fun <A, B : Extract<A>, C> TypedResult<B>.map(block: (A) -> C): List<C> =
  result(this.columns)
    .map { block(it.getValue(columns.single())) }

fun <A, B, C> TypedResult<And<Extract<A>, Extract<B>>>.map(block: (A, B) -> C): List<C> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1])
      )
    }

fun <A, B, C, D> TypedResult<And<And<Extract<A>, Extract<B>>, Extract<C>>>.map(block: (A, B, C) -> D): List<D> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2])
      )
    }

fun <A, B, C, D, E> TypedResult<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>>.map(block: (A, B, C, D) -> E): List<E> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3])
      )
    }

fun <A, B, C, D, E, F> TypedResult<
  And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>,
  >.map(block: (A, B, C, D, E) -> F): List<F> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3]),
        it.getValue(columns[4]),
      )
    }

fun <A, B, C, D, E, F, G> TypedResult<
  And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<G>>,
  >.map(block: (A, B, C, D, E, F) -> G): List<G> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3]),
        it.getValue(columns[4]),
        it.getValue(columns[5]),
      )
    }

fun <A, B, C, D, E, F, G, H> TypedResult<
  And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<G>>, Extract<H>>,
  >.map(block: (A, B, C, D, E, F, G) -> H): List<H> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3]),
        it.getValue(columns[4]),
        it.getValue(columns[5]),
        it.getValue(columns[6]),
      )
    }

fun <A, B, C, D, E, F, G, H, I> TypedResult<
  And<And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<G>>, Extract<H>>, Extract<I>>,
  >.map(block: (A, B, C, D, E, F, G, H) -> I): List<I> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3]),
        it.getValue(columns[4]),
        it.getValue(columns[5]),
        it.getValue(columns[6]),
        it.getValue(columns[7]),
      )
    }

fun <A, B, C, D, E, F, G, H, I, J> TypedResult<
  And<And<And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<G>>, Extract<H>>, Extract<I>>, Extract<J>>,
  >.map(block: (A, B, C, D, E, F, G, H, I) -> J): List<J> =
  result(this.columns)
    .map {
      block(
        it.getValue(columns[0]),
        it.getValue(columns[1]),
        it.getValue(columns[2]),
        it.getValue(columns[3]),
        it.getValue(columns[4]),
        it.getValue(columns[5]),
        it.getValue(columns[6]),
        it.getValue(columns[7]),
        it.getValue(columns[8]),
      )
    }
