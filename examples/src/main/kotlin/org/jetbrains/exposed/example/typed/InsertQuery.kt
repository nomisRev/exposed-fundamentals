package org.jetbrains.exposed.example.typed

import org.jetbrains.exposed.example.typed.ResultSpec.And
import org.jetbrains.exposed.example.typed.ResultSpec.Extract
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.jdbc.insertReturning

fun <T : Table> T.insertReturning(
  ignoreErrors: Boolean = false,
  block: T.(InsertStatement<Number>) -> Unit
): TypedExecutable<ResultSpec.Empty> = TypedExecutable([], {
  this@insertReturning.insertReturning(returning = it, ignoreErrors, block)
})

fun <A> TypedExecutable<ResultSpec.Empty>.select(column: Column<A>): TypedExecutable<Extract<A>> =
  TypedExecutable(columns + column, result)

@JvmName("selectEntityId")
fun <A : Any, ID : EntityID<A>> TypedExecutable<ResultSpec.Empty>.select(column: Column<ID>): TypedExecutable<Extract<A>> =
  TypedExecutable(columns + column, result)

fun <A, B> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<A>,
  columnB: Column<B>
): TypedExecutable<And<Extract<A>, Extract<B>>> =
  TypedExecutable(columns + columnA + columnB, result)

@JvmName("selectEntityIdFirstOfTwo")
fun <A : Any, ID : EntityID<A>, B> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<ID>,
  columnB: Column<B>
): TypedExecutable<And<Extract<A>, Extract<B>>> =
  TypedExecutable(columns + columnA + columnB, result)

@JvmName("selectEntityIdSecondOfTwo")
fun <A, B : Any, ID : EntityID<B>> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<A>,
  columnB: Column<ID>
): TypedExecutable<And<Extract<A>, Extract<B>>> =
  TypedExecutable(columns + columnA + columnB, result)

fun <A, B, C> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<A>,
  columnB: Column<B>,
  columnC: Column<C>
): TypedExecutable<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedExecutable(columns + columnA + columnB + columnC, result)

@JvmName("selectEntityIdFirstOfThree")
fun <A : Any, ID : EntityID<A>, B, C> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<ID>,
  columnB: Column<B>,
  columnC: Column<C>
): TypedExecutable<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedExecutable(columns + columnA + columnB + columnC, result)

@JvmName("selectEntityIdSecondOfThree")
fun <A, B : Any, ID : EntityID<B>, C> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<A>,
  columnB: Column<ID>,
  columnC: Column<C>
): TypedExecutable<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedExecutable(columns + columnA + columnB + columnC, result)

@JvmName("selectEntityIdThirdOfThree")
fun <A, B, C : Any, ID : EntityID<C>> TypedExecutable<ResultSpec.Empty>.select(
  columnA: Column<A>,
  columnB: Column<B>,
  columnC: Column<ID>
): TypedExecutable<And<And<Extract<A>, Extract<B>>, Extract<C>>> =
  TypedExecutable(columns + columnA + columnB + columnC, result)
