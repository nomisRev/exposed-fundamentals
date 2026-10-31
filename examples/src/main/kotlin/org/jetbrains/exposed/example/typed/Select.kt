package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.EntityID

fun <A : ResultSpec, B> TypedQuery<A>.select(columnB: Column<B>): TypedQuery<And<A, Extract<B>>> =
  TypedQuery(columns + columnB, result)

fun <A : ResultSpec, B, C> TypedQuery<A>.select(
  columnB: Column<B>,
  columnC: Column<C>
): TypedQuery<And<And<A, Extract<B>>, Extract<C>>> =
  TypedQuery(columns + columnB + columnC, result)

@JvmName("selectEntityIdFirst")
fun <A : ResultSpec, B : Any, ID : EntityID<B>, C> TypedQuery<A>.select(
  columnB: Column<ID>,
  columnC: Column<C>
): TypedQuery<And<And<A, Extract<B>>, Extract<C>>> =
  TypedQuery(columns + columnB + columnC, result)

@JvmName("selectEntityIdSecond")
fun <A : ResultSpec, B, C : Any, ID : EntityID<C>> TypedQuery<A>.select(
  columnB: Column<B>,
  columnC: Column<ID>
): TypedQuery<And<And<A, Extract<B>>, Extract<C>>> =
  TypedQuery(columns + columnB + columnC, result)

fun <A : ResultSpec, B, C, D> TypedQuery<A>.select(
  columnB: Column<B>,
  columnC: Column<C>,
  columnD: Column<D>,
): TypedQuery<And<And<And<A, Extract<B>>, Extract<C>>, Extract<D>>> =
  TypedQuery(columns + columnB + columnC + columnD, result)
