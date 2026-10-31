package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract

fun <A, B : Extract<A>, C> TypedResult<B>.singleOrNull(block: (A) -> C): C? =
  result(columns).singleOrNull()
    ?.let { row -> block(row.getValue(columns[0])) }

fun <A, B, C> TypedResult<And<Extract<A>, Extract<B>>>.singleOrNull(block: (A, B) -> C): C? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1])
    )
  }

fun <A, B, C, D> TypedResult<And<And<Extract<A>, Extract<B>>, Extract<C>>>.singleOrNull(block: (A, B, C) -> D): D? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2])
    )
  }

fun <A, B, C, D, E> TypedResult<
  And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>,
  >.singleOrNull(block: (A, B, C, D) -> E): E? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
    )
  }

fun <A, B, C, D, E, F> TypedResult<
  And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>,
  >.singleOrNull(block: (A, B, C, D, E) -> F): F? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
      row.getValue(columns[4]),
    )
  }

fun <A, B, C, D, E, F, G> TypedResult<
  And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<F>>,
  >.singleOrNull(block: (A, B, C, D, E, F) -> G): G? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
      row.getValue(columns[4]),
      row.getValue(columns[5]),
    )
  }

fun <A, B, C, D, E, F, G, H> TypedResult<
  And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<F>>, Extract<G>>,
  >.singleOrNull(block: (A, B, C, D, E, F, G) -> H): H? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
      row.getValue(columns[4]),
      row.getValue(columns[5]),
      row.getValue(columns[6]),
    )
  }

fun <A, B, C, D, E, F, G, H, I> TypedResult<
  And<And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<F>>, Extract<G>>, Extract<H>>,
  >.singleOrNull(block: (A, B, C, D, E, F, G, H) -> I): I? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
      row.getValue(columns[4]),
      row.getValue(columns[5]),
      row.getValue(columns[6]),
      row.getValue(columns[7]),
    )
  }

fun <A, B, C, D, E, F, G, H, I, J> TypedResult<
  And<And<And<And<And<And<And<And<Extract<A>, Extract<B>>, Extract<C>>, Extract<D>>, Extract<E>>, Extract<F>>, Extract<G>>, Extract<H>>, Extract<I>>,
  >.singleOrNull(block: (A, B, C, D, E, F, G, H, I) -> J): J? =
  result(columns).singleOrNull()?.let { row ->
    block(
      row.getValue(columns[0]),
      row.getValue(columns[1]),
      row.getValue(columns[2]),
      row.getValue(columns[3]),
      row.getValue(columns[4]),
      row.getValue(columns[5]),
      row.getValue(columns[6]),
      row.getValue(columns[7]),
      row.getValue(columns[8]),
    )
  }
