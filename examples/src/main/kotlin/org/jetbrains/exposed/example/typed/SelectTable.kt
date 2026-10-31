package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.select as ExposedSelect

fun <A> Table.select(column: Column<A>): TypedQuery<Extract<A>> =
  TypedQuery([column], { ExposedSelect(it) })

fun <A : ResultSpec, B : ResultSpec> Table.select(
  columnA: Column<A>,
  columnB: Column<B>
): TypedQuery<And<Extract<A>, Extract<B>>> =
  TypedQuery([columnA, columnB], { ExposedSelect(it) })


fun <A, B, C> Table.select(
  columnA: Column<A>,
  columnB: Column<B>,
  columnC: Column<C>
): TypedQuery<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedQuery([columnA, columnB, columnC], { ExposedSelect(it) })


