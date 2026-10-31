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
