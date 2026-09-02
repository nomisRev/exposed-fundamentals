---
layout: intro
class: section-slide
kodee: wave
---


<div class="lesson-number">Topic 2</div>

# SQL DSL

## Write queries deliberately

---

# SQL-shaped Kotlin

```kotlin
Talks.selectAll()
```

```sql
SELECT *
FROM talks
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
```

```sql
SELECT id, title, startsAt
FROM talks
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :id
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
  .orderBy(Talks.title, SortOrder.ASC)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :id
ORDER BY title ASC
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
  .orderBy(Talks.title, SortOrder.ASC)
  .limit(10)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :id
ORDER BY title ASC
LIMIT 10
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
  .orderBy(Talks.title, SortOrder.ASC)
  .limit(10)
  .offset(30)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :id
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
  .orderBy(Talks.title, SortOrder.ASC)
  .limit(10)
  .offset(30)
  .orWhere { Talks.description like "%Kotlin%" }
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :id OR description LIKE '%Kotlin%'
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
  .where { Talks.id eq id }
  .orderBy(Talks.title, SortOrder.ASC)
  .limit(10)
  .offset(30)
  .orWhere { Talks.description like "%Kotlin%" }
  .adjustSelect {
    select(Talks.id, Talks.speakerId, Talks.title, Talks.description, Talks.startsAt)
  }
```

```sql
SELECT id, speakerId, title, description, startsAt
FROM talks
WHERE id = :id OR description LIKE '%Kotlin%'
ORDER BY title ASC 
LIMIT 10
OFFSET 30
```

---

# SQL-shaped Kotlin

<DrawnAnnotation text="innerJoin" color="var(--drawn-annotation-color)" on="0" />
<DrawnAnnotation text="INNER JOIN" label="`rightJoin leftJoin fullJoin crossJoin`" color="var(--drawn-annotation-color)" on="0" :geometry="{ label: { x: 0.2159, y: 0.5098, width: 0.1646 }, connector: { type: 'quadratic', start: { x: 0.1131, y: 0.3743 }, control: { x: 0.1207, y: 0.4586 }, end: { x: 0.1570, y: 0.5017 } } }" />
<DrawnAnnotation text="{ Talks.speakerId eq ProfileTable.id }" on="1" label="Can be omitted **if** `Talks` only has 1 `reference` to `ProfileTable` but prefer being explicit"  :geometry="{ label: { x: 0.6802, y: 0.5054, width: 0.7271 }, connector: { start: { x: 0.7048, y: 0.2301 }, end: { x: 0.7048, y: 0.4512 } } }"/>

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
```

```sql
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
```

```sql
SELECT talks.title, talks.description, profiles.name, profiles.avatar_url
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id

```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
  .where { Talks.isPublished eq true }
```

```sql
SELECT talks.title, talks.description, profiles.name, profiles.avatar_url
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id
WHERE talks.is_published = TRUE
```

---

# Query

<DrawnAnnotation text="Query" label="Lazy mutable builder allows building SQL" :geometry="{ label: { x: 0.5240, y: 0.1091 }, connector: { type: 'quadratic', start: { x: 0.2942, y: 0.2235 }, control: { x: 0.3516, y: 0.1998 }, end: { x: 0.3908, y: 0.1462 } } }" />

```kotlin
fun previews(): Query =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { Talks.isPublished eq true }
```

---

# Executing the query

<DrawnAnnotation text="Blocking" label="JDBC is a blocking driver" color="#f59e0b"  :geometry="{ label: { x: 0.5960, y: 0.2338 }, connector: { type: 'quadratic', start: { x: 0.1853, y: 0.3160 }, control: { x: 0.5448, y: 0.3221 }, end: { x: 0.5757, y: 0.2641 } } }" />

```kotlin jdbc
class Query(...) :
  SizedIterable<ResultRow>,
  BlockingExecutable<ResultApi, Query>
```

<DrawnAnnotation text="Suspend" label="R2DBC is a reactive driver" color="#06b6d4"  :geometry="{ label: { x: 0.1692, y: 0.5695 } }" />

```kotlin r2dbc
class Query(...) :
  SizedIterable<ResultRow>,
  SuspendExecutable<ResultApi, Query>
```

---

# Executing the query

<DrawnAnnotation text="Iterable" occurrence=2 label="Blocking cursor" color="#f59e0b"  :geometry="{ label: { x: 0.6540, y: 0.2759 }, connector: { start: { x: 0.5124, y: 0.2338 }, end: { x: 0.5677, y: 0.2777 } } }" />

```kotlin jdbc
interface SizedIterable<out T> : Iterable<T> {
  fun limit(count: Int): SizedIterable<T>
  fun offset(start: Long): SizedIterable<T>
  fun count(): Long
  fun empty(): Boolean
}
```

<DrawnAnnotation text="Flow" color="#06b6d4" />
<DrawnAnnotation text="suspend" color="#06b6d4"  />
<DrawnAnnotation text="suspend" occurrence=2 label="R2DBC suspends like reactive types" color="#06b6d4"  :geometry="{ label: { x: 0.2343, y: 0.8620 }, connector: { start: { x: 0.1150, y: 0.7465 }, end: { x: 0.1368, y: 0.8298 } } }" />

```kotlin r2dbc
interface SizedIterable<out T> : Flow<T> {
  fun limit(count: Int): SizedIterable<T>
  fun offset(start: Long): SizedIterable<T>
  suspend fun count(): Long
  suspend fun empty(): Boolean
}
```

---

# Executing the query

> Effectively `Iterable` for JDBC and `Flow` for R2DBC

```kotlin jdbc
class Query(...) : Iterable<ResultRow>
```

```kotlin r2dbc
class Query(...) : Flow<ResultRow>
```

---

# JDBC alongside R2DBC

> Avoid using JDBC and R2DBC in the same Kotlin **file**
> 
> Using them in the same module is possible

<DrawnAnnotation text="jdbc.selectAll as jdbcSelectAll" color="red" />
<DrawnAnnotation text="jdbcSelectAll()" color="red" />
<DrawnAnnotation text="r2dbcSelectAll()" color="red"/>

```kotlin
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll as jdbcSelectAll
import org.jetbrains.exposed.v1.r2dbc.selectAll as r2dbcSelectAll

fun isTalk(id: Long) = Talks.id eq id

fun jdbc(id: Long) =
  Talks.jdbcSelectAll().where { isTalk(id) }

fun r2dbc(id: Long) =
  Talks.r2dbcSelectAll().where { isTalk(id) }

```

---

# Seperate modules

<DrawnAnnotation text="jdbc" color="#f59e0b" />
<DrawnAnnotation text="r2dbc" color="#06b6d4" label="R2DBC and JDBC live alongside each other" :connect="false" :geometry="{ label: { x: 0.5462, y: 0.5702 } }" />

```kotlin jdbc
package org.jetbrains.exposed.v1.jdbc

class Query(...) : Iterable<ResultRow>
```

```kotlin r2dbc
package org.jetbrains.exposed.v1.r2dbc

class Query(...) : Flow<ResultRow>
```

---

# Minimal differences

```kotlin{5-7} jdbc 
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.Query
```

```kotlin{5-7} r2dbc 
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.Query
```

---

# <span color="#f59e0b">JDBC</span> ~ <span color="#06b6d4">R2DBC</span>

> Same code different imports

```kotlin
fun talks(limit: Long = 10, offset: Long = 30): Query =
  Talks.select(Talks.speakerId, Talks.title, Talks.description, Talks.startsAt)
    .where { Talks.description like "%Kotlin%" }
    .orderBy(Talks.title, SortOrder.ASC)
    .limit(limit)
    .offset(offset)

fun previews(): Query =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { Talks.isPublished eq true }
```

---

# Accessing the data

```kotlin
fun previews(): Query =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { isPublished eq true }
```

---
magic-move
---

# Accessing the data

```kotlin
fun previews(): Iterable<ResultRow> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { isPublished eq true }
```

---
magic-move
---

# Accessing the data

```kotlin
fun previews(): List<?> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { isPublished eq true }
    .map { row: ResultRow -> TODO("Return value") }
```

---
magic-move
---

# Accessing the data

```kotlin
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { isPublished eq true }
    .map { row: ResultRow -> row.toTalkPreview() }

data class TalkPreview(
  val title: String,
  val description: String,
  val speakerName: Instant,
  val speakerAvatarUrl: String,
)
```

---

# Accessing the data

<DrawnAnnotation text="Column<A>): A" label="Typed data retrieval from `ResultRow`"  :geometry="{ label: { x: 0.6103, y: 0.3744, width: 0.4137 } }" />

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview(): TalkPreview
```

---
magic-move
---

# Accessing the data

<DrawnAnnotation text="title = " label="Expects `String`" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.1588, y: 0.4849, width: 0.1926 }, connector: { start: { x: 0.1248, y: 0.3758 }, end: { x: 0.1174, y: 0.4542 } } }" />
<DrawnAnnotation text="Talks.title" label="`Column<String>`"  :geometry="{ label: { x: 0.3554, y: 0.4771, width: 0.1521 }, connector: { start: { x: 0.3208, y: 0.3773 }, end: { x: 0.3205, y: 0.4444 } } }" />

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
  title = this.get(Talks.title),
) 
```

---
magic-move
---

# Accessing the data

<DrawnAnnotation text="operator" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="[Talks.title]" label="Kotlin index access operator" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.4534, y: 0.4627 } }" />

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
  title = this[Talks.title],
) 
```

---
magic-move
---

# Accessing the data

<DrawnAnnotation text="this[ProfileTable.avatarUrl]" label="Access all 'select' columns from our join by referencing multiple tables"  :geometry="{ label: { x: 0.4421, y: 0.6113 } }"/>

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
  title = this[Talks.title],
  description = this[Talks.description],
  speakerName = this[ProfileTable.name],
  speakerAvatarUrl = this[ProfileTable.avatarUrl],
) 
```

---

# Accessing the data

```kotlin
fun ResultRow.toTalkPreview() = TalkPreview(
  title = this[Talks.title],
  description = this[Talks.description],
  speakerName = this[ProfileTable.name],
  speakerAvatarUrl = this[ProfileTable.avatarUrl],
)

fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { row: ResultRow -> row.toTalkPreview() }
```

<v-clicks>

<div class="stacktrace" aria-label="Application stack trace">
  <div>Exception in thread "main" <span class="stacktrace-link">java.lang.IllegalStateException</span>: No transaction in context.</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager$Companion.current(<span class="stacktrace-link">TransactionManager.kt:173</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.Query.getTransaction(<span class="stacktrace-link">Query.kt:29</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.Query.iterator(<span class="stacktrace-link">Query.kt:302</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at kotlin.collections.CollectionsKt___CollectionsKt.toCollection(<span class="stacktrace-link">_Collections.kt:1479</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at kotlin.collections.CollectionsKt___CollectionsKt.toMutableList(<span class="stacktrace-link">_Collections.kt:1512</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at kotlin.collections.CollectionsKt___CollectionsKt.toList(<span class="stacktrace-link">_Collections.kt:1503</span>)</div>
</div>

</v-clicks>

---

# Database work requires a transaction

> A transaction is one atomic unit of work,
>
> commit together or roll back together

<DrawnAnnotation text=" JdbcTransaction.()" />
<DrawnAnnotation text=" R2dbcTransaction.()" />

```kotlin jdbc
transaction { /* JdbcTransaction.() -> Unit */
  // Query can execute here
}
```

```kotlin r2dbc
suspendTransaction { /* R2dbcTransaction.() -> Unit */
  // Query can execute here
}
```

---

# Database work requires a transaction

<DrawnAnnotation text="previews" :connect="false" label="Can be called from anywhere resulting in java.lang.IllegalStateException: No transaction in context." color="red"  :geometry="{ label: { x: 0.3899, y: 0.4873, width: 0.5693 }, connector: { type: 'quadratic', start: { x: 0.0821, y: 0.2209 }, control: { x: 0.1248, y: 0.4257 }, end: { x: 0.2179, y: 0.4845 } } }" />

```kotlin
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }
```

---
magic-move
---

# Database work requires a transaction

<DrawnAnnotation text="context(_: Transaction)" label="Compile-time guarantee it's called inside a `Transaction` (common supertype)"  :geometry="{ label: { x: 0.4139, y: 0.5362, width: 0.7506 }, connector: { type: 'quadratic', start: { x: 0.0496, y: 0.2369 }, control: { x: 0.0130, y: 0.3788 }, end: { x: 0.0789, y: 0.4924 } } }" />

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }
```

---
magic-move
---

# Database work requires a transaction

<InlineCompilerError text="previews" occurrence=2 message="No context argument for '_: Transaction' found">

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }

fun example() = previews()
```

</InlineCompilerError>

---
magic-move
---

# Database work requires a transaction

<InlineCompilerError text="previews" occurrence=2 message="No context argument for '_: Transaction' found">

<DrawnAnnotation text="JdbcTransaction" label="`Transaction` context argument" :geometry="{ label: { x: 0.5209, y: 0.6608, width: 0.4712 }, connector: { start: { x: 0.4819, y: 0.6106 }, end: { x: 0.4828, y: 0.6461 } } }" />
<DrawnAnnotation text="Please call Database.connect() first or specify a database explicitly in the transaction call" at="1"  />

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }

fun example() = previews()

fun example2() = transaction { /* JdbcTransaction.() -> Unit */
  previews()
}
```

<v-clicks>

<div class="stacktrace" aria-label="Application stack trace">
  <div>Exception in thread "main" <span class="stacktrace-link">java.lang.IllegalStateException</span>: No database specified and no default database found.<br />Please call Database.connect() first or specify a database explicitly in the transaction call.</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.resolveDatabaseOrThrow(<span class="stacktrace-link">Transactions.kt:108</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.transaction(<span class="stacktrace-link">Transactions.kt:139</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.transaction$default(<span class="stacktrace-link">Transactions.kt:133</span>)</div>
</div>

</v-clicks>

</InlineCompilerError>

---

# Database work requires a transaction

<DrawnAnnotation text="(database)" label="explicitly specified database in the transaction call"  :geometry="{ connector: { start: { x: 0.5941, y: 0.5140 }, end: { x: 0.5941, y: 0.5414 } } }" />
<DrawnAnnotation text="(database: Database)" label="But where is `Database` coming from?" on="1"  :geometry="{ label: { x: 0.2649, y: 0.6217 }, connector: { start: { x: 0.2649, y: 0.5216 }, end: { x: 0.2649, y: 0.5912 } } }" />

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }

fun example(database: Database) = transaction(database) { previews() }
```

---

# Connecting a database

```kotlin jdbc
val jdbc = Database.connect(
  url = "jdbc:postgresql://localhost:5432/example",
  user = "postgresql",
  password = "password",
)
```

```kotlin r2dbc
val r2dbc = R2dbcDatabase.connect(
  url = "r2dbc:postgresql://localhost:5432/example",
  user = "postgresql",
  password = "password",
)
```

---
magic-move
---

# Connecting a database

```kotlin jdbc
val jdbc = Database.connect(
  url = "jdbc:postgresql://localhost:5432/example",
  user = "postgresql",
  password = "password",
  databaseConfig = DatabaseConfig {
    sqlLogger = Slf4jSqlDebugLogger
    defaultMaxAttempts = 1
  }
)
```

```kotlin r2dbc
val r2dbc = R2dbcDatabase.connect(
  url = "r2dbc:postgresql://localhost:5432/example",
  user = "postgresql",
  password = "password",
  databaseConfig = R2dbcDatabaseConfig {
    sqlLogger = Slf4jSqlDebugLogger
    defaultR2dbcIsolationLevel = IsolationLevel.READ_COMMITTED
  }
)
```

---

# Database work requires a transaction

<DrawnAnnotation text="transaction {" label="Default database found (from `Database.connect`)"  :geometry="{ label: { x: 0.2816, y: 0.6992, width: 0.4562 }, connector: { start: { x: 0.2802, y: 0.6085 }, end: { x: 0.2796, y: 0.6594 } } }" />
<DrawnAnnotation text="transaction(database)" label="Prefer explicitly passing database instead of relying on implicit resolution"  :geometry="{ label: { x: 0.7216, y: 0.7197, width: 0.4910 }, connector: { start: { x: 0.5509, y: 0.5191 }, end: { x: 0.6255, y: 0.6776 } } }" />

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }

fun example(database: Database) = transaction(database) { previews() }

fun example() = transaction { previews() }
```

---

# Database work requires a transaction

<DrawnAnnotation text="@Transactional" label="Spring's `@Transactional` can also provide Exposed `Transaction`"  :geometry="{ label: { x: 0.3657, y: 0.4912, width: 0.6311 }, connector: { type: 'quadratic', start: { x: 0.0426, y: 0.2194 }, control: { x: 0.0212, y: 0.3626 }, end: { x: 0.0553, y: 0.4803 } } }" />

```kotlin
@Transactional
fun previews(): List<TalkPreview> =
  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }
```

---

# Insert a talk

<DrawnAnnotation text="insert" occurrence=2 label="`InsertStatement.() -> Unit` builder"  :geometry="{ label: { x: 0.4500, y: 0.5148, width: 0.3736 }, connector: { type: 'quadratic', start: { x: 0.1938, y: 0.4732 }, control: { x: 0.2046, y: 0.5192 }, end: { x: 0.2655, y: 0.5144 } } }" />

```kotlin
context(_: Transaction)
fun insert(
  title: String,
  speakerId: Long,
  startsAt: Instant
) = Talks.insert {

  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
```

---

# Insert a talk

<DrawnAnnotation text="operator" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="set" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="Column<S>, value: S" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="it[Talks.title] = title" label="Typesafe operator setter using `Column<String>`" color="var(--drawn-annotation-color)" sequential="false" :geometry="{ label: { x: 0.6161, y: 0.5807, width: 0.5215 }, connector: { start: { x: 0.3598, y: 0.5983 }, end: { x: 0.3880, y: 0.5828 } } }" />

```kotlin
operator fun <S> InsertStatement.set(column: Column<S>, value: S) = TODO("")

context(_: Transaction)
fun insert(
  title: String,
  speakerId: Long,
  startsAt: Instant
)  = Talks.insert {
    it[Talks.title] = title
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
```

---

# Insert a talk

<DrawnAnnotation text="InsertStatement" label="The executed insert statement, providing the affected-row count and any available generated values"  :geometry="{ label: { x: 0.6443, y: 0.5023, width: 0.4733 }, connector: { start: { x: 0.2464, y: 0.4736 }, end: { x: 0.4694, y: 0.5114 } } }" />

```kotlin
context(_: Transaction)
fun insert(
  title: String,
  speakerId: Long,
  startsAt: Instant
): InsertStatement<Number> = Talks.insert {
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
```

---

# Insert and get id

<DrawnAnnotation text="EntityID<Long>" label="Returns `EntityID` not raw Long" :connect="false" :geometry="{ label: { x: 0.4351, y: 0.3666, width: 0.2986 } }" />

```kotlin
context(_: Transaction)
fun insertAndGetId(
  title: String,
  speakerId: Long,
  startsAt: Instant
): EntityID<Long> = Talks.insertAndGetId {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING id
```

---
magic-move
---

# Insert and get id

<DrawnAnnotation text=".value" label="Explicitly unwrap" />

```kotlin
context(_: Transaction)
fun insertAndGetId(
  title: String,
  speakerId: Long,
  startsAt: Instant
): Long = Talks.insertAndGetId {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }.value
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING id
```

---

# Insert a talk — return what you need

<DrawnAnnotation text="listOf(Talks.id, Talks.createdAt, Talks.updatedAt)" label="Be explicit about returned data"  :geometry="{ label: { x: 0.6771, y: 0.5660, width: 0.2889 } }" />

```kotlin
context(_: Transaction)
fun insertReturning(
  title: String,
  speakerId: Long,
  startsAt: Instant
): Iterable<ResultRow> =
  Talks.insertReturning(listOf(Talks.id, Talks.createdAt, Talks.updatedAt)) {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING id, createdAt, updatedAt
```

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="[Talks.id, Talks.createdAt, Talks.updatedAt]" label="Use collection literals if you do!"   :geometry="{ label: { x: 0.6844, y: 0.6180 } }"/>

```kotlin
context(_: Transaction)
fun insertReturning(
  title: String,
  speakerId: Long,
  startsAt: Instant
): Iterable<ResultRow> =
  Talks.insertReturning([Talks.id, Talks.createdAt, Talks.updatedAt]) {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING id, createdAt, updatedAt
```

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="RETURNING *" />

```kotlin
context(_: Transaction)
fun insertReturning(
  title: String,
  speakerId: Long,
  startsAt: Instant
): Iterable<ResultRow> =
  Talks.insertReturning {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING *
```

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="TalkWithSpeakerId" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="map(ResultRow::toTalkWithSpeakerId)" label="Return all data typed" color="var(--drawn-annotation-color)" sequential="false" :geometry="{ label: { x: 0.6679, y: 0.6723, width: 0.3424 }, connector: { start: { x: 0.4946, y: 0.7374 }, end: { x: 0.5683, y: 0.6904 } } }" />

```kotlin
context(_: Transaction)
fun insertReturning(
  title: String,
  speakerId: Long,
  startsAt: Instant
): List<TalkWithSpeakerId> =
  Talks.insertReturning {
    it[Talks.title] = title
    it[Talks.slug] = title.toUniqueSlug()
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }.map(ResultRow::toTalkWithSpeakerId)
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING *
```


---

# Insert a talk — do nothing if exists

<DrawnAnnotation text="insertIgnore" />
<DrawnAnnotation text="ON CONFLICT DO NOTHING" />

```kotlin
context(_: Transaction)
fun insert(
  title: String,
  speakerId: Long,
  startsAt: Instant
) = Talks.insertIgnore {
  it[Talks.title] = title
  it[Talks.speakerId] = speakerId
  it[Talks.startsAt] = startsAt
}
```
```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
ON CONFLICT DO NOTHING;
```

---
magic-move
---

# Insert a talk — do nothing if exists

<DrawnAnnotation text="AndGetId" />
<DrawnAnnotation text="RETURNING id" />

```kotlin
Talks.insertIgnoreAndGetId {
  it[Talks.title] = "Another title"
  it[Talks.speakerId] = 99
  it[Talks.startsAt] = Instant.parse("2026-10-01T10:00:00Z")
}
```

```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES ('Another title', 99, TIMESTAMP '2026-10-01 10:00:00')
ON CONFLICT DO NOTHING
RETURNING id;
```

---
magic-move
---

# Insert a talk — do nothing if exists

<DrawnAnnotation text="insertReturning" />
<DrawnAnnotation text="ignoreErrors" label="Normal insertReturning with a flag"  :geometry="{ label: { x: 0.7593, y: 0.2737 } }"/>

```kotlin
Talks.insertReturning([Talks.id, Talks.createdAt], ignoreErrors = true) {
  it[Talks.title] = "Another title"
  it[Talks.speakerId] = 99
  it[Talks.startsAt] = Instant.parse("2026-10-01T10:00:00Z")
}
```

```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES ('Another title', 99, TIMESTAMP '2026-10-01 10:00:00')
ON CONFLICT DO NOTHING
RETURNING id, createdAt;
```

---

# Insert collections with `batchInsert`

<DrawnAnnotation text="ResultRow::toTalkWithoutSpeaker" label="Batch insert while returning all inserted data"  :geometry="{ label: { x: 0.7393, y: 0.4750 }, connector: { start: { x: 0.4938, y: 0.5017 }, end: { x: 0.5291, y: 0.4829 } } }"/>
<DrawnAnnotation text="RETURNING *" label="JDBC `RETURN_GENERATED_KEYS`: the PostgreSQL driver appends `RETURNING *` itself" on="0" />

```kotlin
context(_: Transaction)
fun insertBatch(newTalk: List<NewTalk>) =
  Talks.batchInsert(newTalk) { newTalk ->
    this[Talks.title] = newTalk.title
    this[Talks.speakerId] = newTalk.speakerId
    this[Talks.startsAt] = newTalk.startsAt
  }.map(ResultRow::toTalkWithoutSpeaker)
```

```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title, :speakerId, :startsAt)
RETURNING *
```


---
magic-move
---

# Insert collections with `batchInsert`

<DrawnAnnotation text="shouldReturnGeneratedValues = false" label="The PostgreSQL driver merges the JDBC batch into multi-row statements `reWriteBatchedInserts=true`"  :geometry="{ label: { x: 0.6125, y: 0.5222, width: 0.7326 }, connector: { start: { x: 0.5850, y: 0.3285 }, end: { x: 0.6164, y: 0.4801 } } }"/>

```kotlin
context(_: Transaction)
fun insertBatch(newTalk: List<NewTalk>) {
  Talks.batchInsert(newTalk, shouldReturnGeneratedValues = false) { newTalk ->
    this[Talks.title] = newTalk.title
    this[Talks.speakerId] = newTalk.speakerId
    this[Talks.startsAt] = newTalk.startsAt
  }
}
```

```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES (:title1, :speakerId1, :startsAt1),
       (:title2, :speakerId2, :startsAt2)
```

---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Without a precise predicate can affect many rows"  :geometry="{ label: { x: 0.5046, y: 0.3477, width: 0.4722 }, connector: { start: { x: 0.3012, y: 0.2769 }, end: { x: 0.3012, y: 0.3179 } } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus) =
  Talks.update({ Talks.id eq talkId }) {
    it[Talks.status] = status
  }
```

```sql
UPDATE talks
SET status = :status
WHERE id = :talkId
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="it[Talks.status] = status" label="Same DSL as insert"  :geometry="{ label: { x: 0.5105, y: 0.3388, width: 0.1764 }, connector: { start: { x: 0.3820, y: 0.3171 }, end: { x: 0.4182, y: 0.3377 } } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus) =
  Talks.update({ Talks.id eq talkId }) {
    it[Talks.status] = status
  }
```

```sql
UPDATE talks
SET status = :status
WHERE id = :talkId
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="[Talks.speakerId, Talks.title, Talks.startsAt]" label="Use `*Returning` variant to return selected columns"  :geometry="{ label: { x: 0.6756, y: 0.4016, width: 0.4826 } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
  Talks.updateReturning(
    [Talks.speakerId, Talks.title, Talks.startsAt],
    { Talks.id eq talkId }
  ) {
    it[Talks.status] = status
  }.singleOrNull()?.toTalkPreview()
```

```sql
UPDATE talks
SET status = :status
WHERE id = :talkId
RETURNING speaker_id, title, starts_at
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Predicate guarantees 1 or 0 rows will be affected"  :geometry="{ label: { x: 0.6356, y: 0.4099 }, connector: { start: { x: 0.3484, y: 0.3613 }, end: { x: 0.4022, y: 0.3901 } } }"/>
<DrawnAnnotation text="singleOrNull()"/>

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
  Talks.updateReturning(
    [Talks.speakerId, Talks.title, Talks.startsAt],
    { Talks.id eq talkId }
  ) {
    it[Talks.status] = status
  }.singleOrNull()?.toTalkPreview()
```

```sql
UPDATE talks
SET status = :status
WHERE id = :talkId
RETURNING speaker_id, title, starts_at
```

---

# Upsert states the conflict key

<DrawnAnnotation text="ON CONFLICT DO" label="All unique fields"  :geometry="{ label: { x: 0.60, y: 0.66 }, connector: { start: { x: 0.22, y: 0.68 }, end: { x: 0.44, y: 0.66 } } }"/>
<DrawnAnnotation text="UPDATE SET" />

```kotlin
fun upsert(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsert {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT DO
UPDATE SET
  slug = EXCLUDED.slug,
  title = EXCLUDED.title,
  speaker_id = EXCLUDED.speaker_id,
  starts_at = EXCLUDED.starts_at
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="Talks.slug" label="Specify unique key on which to update the row on conflict"  :geometry="{ label: { x: 0.6542, y: 0.3427 }, connector: { start: { x: 0.3397, y: 0.2780 }, end: { x: 0.3709, y: 0.3169 } } }"/>
<DrawnAnnotation text="(slug)" label="Must have `UNIQUE` constraint"  :geometry="{ label: { x: 0.4897, y: 0.7424 }, connector: { start: { x: 0.2585, y: 0.6949 }, end: { x: 0.3420, y: 0.7338 } } }"/>

```kotlin
fun upsert(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsert(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO
UPDATE SET
  title = EXCLUDED.title,
  speaker_id = EXCLUDED.speaker_id,
  starts_at = EXCLUDED.starts_at
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="onUpdateExclude" label="By default every inserted column is updated, except the conflict key" on="0" :geometry="{ label: { x: 0.7158, y: 0.3958, width: 0.4959 }, connector: { start: { x: 0.4484, y: 0.2864 }, end: { x: 0.5500, y: 0.3800 } } }" />

```kotlin
fun upsert(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsert(Talks.slug, onUpdateExclude = [Talks.speakerId]) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO
UPDATE SET
  title = EXCLUDED.title,
  starts_at = EXCLUDED.starts_at
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="[Talks.id," label="Application-generated values (`clientDefault`, `UuidTable` id) are inserted too, so exclude them" on="0" :geometry="{ label: { x: 0.7158, y: 0.3958, width: 0.4959 }, connector: { start: { x: 0.4484, y: 0.2864 }, end: { x: 0.5500, y: 0.3800 } } }"/>

```kotlin
fun upsert(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsert(Talks.slug, onUpdateExclude = [Talks.id, Talks.speakerId]) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO
UPDATE SET
  title = EXCLUDED.title,
  starts_at = EXCLUDED.starts_at
```


---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="onUpdate" label="Take full control of update" on="0"  :geometry="{ label: { x: 0.7024, y: 0.2681 }, connector: { start: { x: 0.4485, y: 0.2664 }, end: { x: 0.5797, y: 0.2710 } } }"/>

```kotlin
fun upsert(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsert(Talks.slug, onUpdate = {
    it[Talks.title] = insertValue(Talks.title)
    it[Talks.updatedAt] = CurrentTimestamp
  }) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO
UPDATE SET
  title = EXCLUDED.title,
  updated_at = CURRENT_TIMESTAMP
```


---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text=".upsertReturning" />
<DrawnAnnotation text="RETURNING *" />

```kotlin
fun upsertReturning(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsertReturning(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO
UPDATE SET
  title = EXCLUDED.title,
  speaker_id = EXCLUDED.speaker_id,
  starts_at = EXCLUDED.starts_at
RETURNING *
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="[Talks.id, Talks.updatedAt]" />
<DrawnAnnotation text="id, updated_at" />

```kotlin
fun upsertReturning(slug: String, title: String, speakerId: Long, startsAt: Instant) =
  Talks.upsertReturning(Talks.slug, returning = [Talks.id, Talks.updatedAt]) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES (:slug, :title, :speakerId, :startsAt)
ON CONFLICT (slug) DO UPDATE SET
  title = EXCLUDED.title,
  speaker_id = EXCLUDED.speaker_id,
  starts_at = EXCLUDED.starts_at
RETURNING id, updated_at
```

---

# Delete states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Without a precise predicate can affect many rows" on="0" color="red"  :geometry="{ label: { x: 0.5875, y: 0.3523, width: 0.7182 } }"/>
<DrawnAnnotation text=": Int" label="`deleteWhere` returns the affected row count" on="1"  :geometry="{ label: { x: 0.6579, y: 0.1886 }, connector: { start: { x: 0.3840, y: 0.2208 }, end: { x: 0.4469, y: 0.2034 } } }"/>

```kotlin
fun delete(talkId: Long): Int =
  Talks.deleteWhere { Talks.id eq talkId }
```

```sql
DELETE FROM talks
WHERE id = :talkId
```

---
magic-move
---

# Delete states its scope

<DrawnAnnotation text="deleteReturning" label="Use `deleteReturning` to return affected rows" :connect="false" :geometry="{ label: { x: 0.5459, y: 0.3481 } }"/>
<DrawnAnnotation text="RETURNING *" />

```kotlin
fun deleteReturning(talkId: Long): Iterable<ResultRow> =
  Talks.deleteReturning { Talks.id eq talkId }
```

```sql
DELETE FROM talks
WHERE id = :talkId
RETURNING *
```

---
magic-move
---

# Delete states its scope

```kotlin
fun deleteReturning(talkId: Long): List<TalkPreview> =
  Talks.deleteReturning { Talks.id eq talkId }
    .map { it.toTalkPreview() }
```

```sql
DELETE FROM talks
WHERE id = :talkId
RETURNING *
```

---

# Keep generated SQL visible

<DrawnAnnotation text="StdOutSqlLogger" label="Before driver optimisations"  :geometry="{ label: { x: 0.5962, y: 0.2978 } }"/>

```kotlin
transaction(database) {
  addLogger(StdOutSqlLogger)
  Talks.select(Talks.title)
    .where { Talks.isPublished eq true }
    .toList()
}
```
```console
SQL: SELECT talks.title FROM talks WHERE talks.is_published = TRUE
```

---

# The SQL DSL stays visible by design

- `transaction`
- `insert` ~ `SELECT`, `update` ~ `UPDATE`, `delete` ~ `DELETE`
- `where`, `having`, ... → typed predicates
- `ResultRow` → application data

> **Exposed does not hide the query.**
>
> It represents and composes it in typed Kotlin.
