package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.select as ExposedSelect

fun <A : Any, ID : EntityID<A>> Table.select(column: Column<ID>): TypedQuery<Extract<A>> =
  TypedQuery([column], { ExposedSelect(it) })

fun <A : Any, ID : EntityID<A>, C> Table.select(
  columnA: Column<ID>,
  columnB: Column<C>,
): TypedQuery<And<Extract<A>, Extract<C>>> =
  TypedQuery([columnA, columnB], { ExposedSelect(it) })

fun <A : Any, ID : EntityID<A>> Table.select(
  columnA: Column<A>,
  columnB: Column<ID>,
): TypedQuery<And<Extract<A>, Extract<A>>> =
  TypedQuery([columnA, columnB], { ExposedSelect(it) })

fun <A : Any, ID : EntityID<A>, C, D> Table.select(
  columnA: Column<ID>,
  columnB: Column<C>,
  columnC: Column<D>
): TypedQuery<And<And<Extract<A>, Extract<C>>, Extract<D>>> =
  TypedQuery([columnA, columnB, columnC], { ExposedSelect(it) })


fun <A : Any, ID : EntityID<A>, C> Table.select(
  columnA: Column<A>,
  columnB: Column<ID>,
  columnC: Column<C>
): TypedQuery<And<And<Extract<A>, Extract<A>>, Extract<C>>> =
  TypedQuery([columnA, columnB, columnC], { ExposedSelect(it) })

fun <A : Any, B, ID : EntityID<A>> Table.select(
  columnA: Column<A>,
  columnB: Column<B>,
  columnC: Column<ID>
): TypedQuery<And<And<Extract<A>, Extract<B>>, Extract<A>>> =
  TypedQuery([columnA, columnB, columnC], { ExposedSelect(it) })