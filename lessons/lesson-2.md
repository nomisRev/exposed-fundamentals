---
layout: intro
class: section-slide
kodee: wave
---


<div class="lesson-number">Topic 2</div>

# Everyday SQL DSL

## Write ordinary queries deliberately

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

<DrawnAnnotation text="innerJoin" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="INNER JOIN" label="`rightJoin leftJoin fullJoin crossJoin`" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.2168, y: 0.4673, width: 0.1646 }, connector: { type: 'quadratic', start: { x: 0.1153, y: 0.3457 }, control: { x: 0.1229, y: 0.4300 }, end: { x: 0.1592, y: 0.4731 } } }" />

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

<DrawnAnnotation text="Query" label="Lazy mutable builder allows building SQL" :geometry="{ label: { x: 0.5240, y: 0.1091 }, connector: { type: 'quadratic', start: { x: 0.2712, y: 0.2178 }, control: { x: 0.3286, y: 0.1941 }, end: { x: 0.3609, y: 0.1519 } } }" />

```kotlin
fun previews(): Query =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { Talks.isPublished eq true }
```

---

# Executing the query

<DrawnAnnotation text="Blocking" label="JDBC is a blocking driver" color="#f59e0b"  :geometry="{ label: { x: 0.5845, y: 0.2111 }, connector: { type: 'quadratic', start: { x: 0.1853, y: 0.3019 }, control: { x: 0.5448, y: 0.3080 }, end: { x: 0.5757, y: 0.2500 } } }" />

```kotlin jdbc
class Query(...) :
    SizedIterable<ResultRow>,
    BlockingExecutable<ResultApi, Query>
```

<DrawnAnnotation text="Suspend" label="R2DBC is a reactive driver" color="#06b6d4"  :geometry="{ label: { x: 0.2150, y: 0.5444 } }" />

```kotlin r2dbc
class Query(...) :
    SizedIterable<ResultRow>,
    SuspendExecutable<ResultApi, Query>
```

---

# Executing the query

<DrawnAnnotation text="Iterable" occurrence=2 label="Blocking cursor" color="#f59e0b"  :geometry="{ label: { x: 0.5473, y: 0.1190 }, connector: { type: 'polyline', points: [{ x: 0.4427, y: 0.1752 }, { x: 0.4750, y: 0.1193 }] } }" />

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
<DrawnAnnotation text="suspend" occurrence=2 label="R2DBC suspends like reactive types" color="#06b6d4"  :geometry="{ label: { x: 0.2388, y: 0.7998 } }" />

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

> Effectively Iterable for JDBC and Flow for R2DBC

```kotlin jdbc
class Query(...) : Iterable<ResultRow>
```

```kotlin r2dbc
class Query(...) : Flow<ResultRow>
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

```kotlin
fun talks(limit: Long = 10, offset: Long = 30): Query =
    Talks.select(Talks.speakerId, Talks.title, Talks.description, Talks.startsAt)
        .where { Talks.description like "%Kotlin%" }
        .orderBy(Talks.title, SortOrder.ASC)
        .limit(10)
        .offset(30)

fun previews(): Query =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { Talks.isPublished eq true }
```

---

# Accessing the data

```kotlin
fun previews(): Query =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { isPublished eq true }
```

---
magic-move
---

# Accessing the data

```kotlin
fun previews(): Iterable<ResultRow> =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { isPublished eq true }
```

---
magic-move
---

# Accessing the data

```kotlin
fun previews(): List<?> =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
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
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
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

<DrawnAnnotation text="Column<A>): A" label="Typed data retrieval from ResultRow"  :geometry="{ label: { x: 0.6103, y: 0.3744, width: 0.3417 } }" />

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview(): TalkPreview
```

---
magic-move
---

# Accessing the data

<DrawnAnnotation text="title = " label="String" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.1326, y: 0.4427, width: 0.0569 } }" />
<DrawnAnnotation text="Talks.title" label="Column<String>"  :geometry="{ label: { x: 0.3207, y: 0.4405, width: 0.1521 }, connector: { type: 'polyline', points: [{ x: 0.3208, y: 0.3513 }, { x: 0.3205, y: 0.4184 }] } }" />

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
<DrawnAnnotation text="[Talks.title]" label="Kotlin index access operator" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.4532, y: 0.4426 } }" />

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

```kotlin jdbc
transaction { /* Transaction.() -> Unit */
    // Query can execute here
}
```

```kotlin r2dbc
suspendTransaction { /* Transaction.() -> Unit */
    // Query can execute here
}
```

---

# Database work requires a transaction

<DrawnAnnotation text="previews" :connect="false" label="Can be called from anywhere resulting in java.lang.IllegalStateException: No transaction in context." color="red"  :geometry="{ label: { x: 0.3899, y: 0.4872, width: 0.3896 }, connector: { type: 'quadratic', start: { x: 0.0821, y: 0.2209 }, control: { x: 0.1248, y: 0.4257 }, end: { x: 0.2179, y: 0.4845 } } }" />

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

<DrawnAnnotation text="context(_: Transaction)" label="Compile-time guarantee it's called inside a `Transaction`"  :geometry="{ label: { x: 0.2126, y: 0.4520, width: 0.3896 }, connector: { type: 'quadratic', start: { x: 0.0487, y: 0.2127 }, control: { x: 0.0121, y: 0.3546 }, end: { x: 0.0787, y: 0.4554 } } }" />

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

<DrawnAnnotation text="Transaction" occurrence=2 label="`Transaction` context argument" :geometry="{ connector: { type: 'polyline', points: [{ x: 0.4535, y: 0.5655 }, { x: 0.4535, y: 0.5928 }] } }" />
<DrawnAnnotation text="Please call Database.connect() first or specify a database explicitly in the transaction call" at="1"  />

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }

fun example() = previews()

fun example2() = transaction { /* Transaction.() -> Unit */
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

<DrawnAnnotation text="(database)" label="explicitly specified database in the transaction call"  :geometry="{ connector: { type: 'polyline', points: [{ x: 0.5608, y: 0.4825 }, { x: 0.5608, y: 0.5099 }] } }" />
<DrawnAnnotation text="(database: Database)" label="But where is this coming from?" on="1"  :geometry="{ label: { x: 0.2649, y: 0.5807 }, connector: { type: 'polyline', points: [{ x: 0.2649, y: 0.4785 }, { x: 0.2649, y: 0.5481 }] } }" />

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

<DrawnAnnotation text="transaction {" label="Default database found (from `Database.connect`)"  :geometry="{ label: { x: 0.2795, y: 0.6414, width: 0.4562 }, connector: { type: 'polyline', points: [{ x: 0.2802, y: 0.5655 }, { x: 0.2796, y: 0.6164 }] } }" />
<DrawnAnnotation text="transaction(database)" label="Prefer explicitly passing database instead of relying on implicit resolution"  :geometry="{ label: { x: 0.7219, y: 0.7151, width: 0.4910 }, connector: { type: 'polyline', points: [{ x: 0.5338, y: 0.4873 }, { x: 0.6118, y: 0.6586 }] } }" />

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

<DrawnAnnotation text="@Transactional" label="Spring's `@Transactional` can also provide Exposed `Transaction`"  :geometry="{ label: { x: 0.3536, y: 0.4769, width: 0.6311 }, connector: { type: 'quadratic', start: { x: 0.0380, y: 0.2161 }, control: { x: 0.0166, y: 0.3593 }, end: { x: 0.0507, y: 0.4770 } } }" />

```kotlin
@Transactional
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```

---

# Insert a talk

<DrawnAnnotation text="insert" occurrence=2 label="`InsertStatement.() -> Unit` builder"  :geometry="{ label: { x: 0.4573, y: 0.3538, width: 0.3736 }, connector: { type: 'quadratic', start: { x: 0.1907, y: 0.3155 }, control: { x: 0.2015, y: 0.3615 }, end: { x: 0.2624, y: 0.3567 } } }" />

```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {

    }
```

---

# Insert a talk

<DrawnAnnotation text="operator" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="set" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="Column<S>, value: S" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="it[Talks.title] = title" label="Typesafe operator setter using `Column<S>`" color="var(--drawn-annotation-color)" sequential="false" :geometry="{ label: { x: 0.4788, y: 0.5693, width: 0.3986 } }" />

```kotlin
operator fun <S> InsertStatement.set(column: Column<S>, value: S) = TODO("")

context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {
        it[Talks.title] = title
    }
```

---

# Insert a talk

<DrawnAnnotation text="InsertStatement" label="The executed insert statement, providing the affected-row count and any available generated values"  :geometry="{ connector: { type: 'polyline', points: [{ x: 0.7698, y: 0.2644 }, { x: 0.7577, y: 0.2932 }] } }" />

```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant): InsertStatement<Number> =
    Talks.insert {
        it[Talks.title] = title
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }
```

---

# Insert and get id

<DrawnAnnotation text="EntityID<Long>" label="Returns `EntityID` not raw Long"  :geometry="{ label: { x: 0.7346, y: 0.3501, width: 0.2986 } }" />

```kotlin
context(_: Transaction)
fun insertAndGetId(title: String, speakerId: Long, startsAt: Instant): EntityID<Long> =
    Talks.insertAndGetId {
        it[Talks.title] = title
        it[Talks.slug] = title.toUniqueSlug()
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }
```

---
magic-move
---

# Insert and get id

<DrawnAnnotation text=".value" label="Explicitly unwrap"  :geometry="{ connector: { type: 'polyline', points: [{ x: 0.1323, y: 0.5220 }, { x: 0.1323, y: 0.5493 }] } }" />

```kotlin
context(_: Transaction)
fun insertAndGetId(title: String, speakerId: Long, startsAt: Instant): Long =
    Talks.insertAndGetId {
        it[Talks.title] = title
        it[Talks.slug] = title.toUniqueSlug()
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }.value
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

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="[Talks.id, Talks.createdAt, Talks.updatedAt]" label="Use collection literals if you do!"  :geometry="{ label: { x: 0.7109, y: 0.5424, width: 0.2931 } }" />

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

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="insertReturning {" label="`RETURNING *`"  :geometry="{ label: { x: 0.4609, y: 0.4361, width: 0.1292 }, connector: { type: 'quadratic', start: { x: 0.3338, y: 0.4768 }, control: { x: 0.3679, y: 0.4772 }, end: { x: 0.3901, y: 0.4598 } } }" />

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

---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="TalkWithSpeakerId" color="var(--drawn-annotation-color)" sequential="false" />
<DrawnAnnotation text="map(ResultRow::toTalkWithSpeakerId)" label="Return all data typed whilst inserting" color="var(--drawn-annotation-color)" sequential="false" :geometry="{ label: { x: 0.5601, y: 0.7715, width: 0.3424 }, connector: { type: 'quadratic', start: { x: 0.3220, y: 0.6937 }, control: { x: 0.3363, y: 0.7618 }, end: { x: 0.3724, y: 0.7696 } } }" />

```kotlin
context(_: Transaction)
fun insertReturning(
    title: String,
    speakerId: Long,
    startsAt: Instant
): List<TalkWithSpeakerId> =
    Talks.insertReturning([Talks.id, Talks.createdAt, Talks.updatedAt]) {
        it[Talks.title] = title
        it[Talks.slug] = title.toUniqueSlug()
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }.map(ResultRow::toTalkWithSpeakerId)
```

---

# Insert a talk — do nothing if exists

```kotlin
Talks.insertIgnore {
    it[Talks.title] = "Another title"
    it[Talks.speakerId] = 99
    it[Talks.startsAt] = Instant.parse("2026-10-01T10:00:00Z")
}
```

```sql
INSERT INTO talks (title, speaker_id, starts_at)
VALUES ('Another title', 99, TIMESTAMP '2026-10-01 10:00:00')
ON CONFLICT DO NOTHING;
```

---
magic-move
---

# Insert a talk — do nothing if exists

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

```kotlin
context(_: Transaction)
fun insertBatch(newTalk: List<NewTalk>) =
    Talks.batchInsert(newTalk) { newTalk ->
        this[Talks.title] = newTalk.title
        this[Talks.speakerId] = newTalk.speakerId
        this[Talks.startsAt] = newTalk.startsAt
    }.map(ResultRow::toTalkWithoutSpeaker)
```

---
magic-move
---

# Insert collections with `batchInsert`

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

---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Without a precise predicate can affect many rows."  :geometry="{ label: { x: 0.5380, y: 0.4855, width: 0.4722 }, connector: { type: 'quadratic', start: { x: 0.3242, y: 0.2578 }, control: { x: 0.3203, y: 0.3567 }, end: { x: 0.3365, y: 0.4422 } } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="it[Talks.status] = TalkStatus.PUBLISHED" label="Same DSL as insert"  :geometry="{ label: { x: 0.4476, y: 0.3693, width: 0.1764 } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="[Talks.speakerId, Talks.title, Talks.startsAt]" label="Use `*Returning` variant to return selected columns"  :geometry="{ label: { x: 0.6590, y: 0.5519, width: 0.4826 }, connector: { type: 'quadratic', start: { x: 0.5255, y: 0.3171 }, control: { x: 0.5806, y: 0.4014 }, end: { x: 0.5807, y: 0.5082 } } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```

---
magic-move
---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" color="var(--drawn-annotation-color)" />
<DrawnAnnotation text="singleOrNull()" label="Predicate guarantees 1 or 0 rows will be affected" color="var(--drawn-annotation-color)" :geometry="{ label: { x: 0.3771, y: 0.5524, width: 0.4562 }, connector: { type: 'polyline', points: [{ x: 0.1802, y: 0.4769 }, { x: 0.2064, y: 0.5211 }] } }" />

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```

---

# Upsert states the conflict key

```kotlin
Talks.upsert {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
}
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES ('intro-to-kotlin', 'Kotlin: A Practical Introduction', 15, TIMESTAMP '2026-09-01 11:00:00')
ON CONFLICT DO
UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at;
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="Talks.slug" label="Specify unique key on which to update the row on conflict" color="var(--drawn-annotation-color)" :geometry="{ label: { x: 0.5627, y: 0.2624, width: 0.3896 }, connector: { type: 'quadratic', start: { x: 0.2933, y: 0.2174 }, control: { x: 0.4002, y: 0.2328 }, end: { x: 0.4179, y: 0.2617 } } }" />
<DrawnAnnotation text="(slug)" label="Must have `UNIQUE` constraint" color="var(--drawn-annotation-color)" :geometry="{ label: { x: 0.5751, y: 0.6642, width: 0.2715 }, connector: { type: 'quadratic', start: { x: 0.2279, y: 0.6090 }, control: { x: 0.3354, y: 0.6151 }, end: { x: 0.4286, y: 0.6528 } } }" />

```kotlin
Talks.upsert(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
}
```
```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES ('intro-to-kotlin', 'Kotlin: A Practical Introduction', 15, TIMESTAMP '2026-09-01 11:00:00')
ON CONFLICT (slug) DO
UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at;
```

---
magic-move
---

# Upsert states the conflict key

```kotlin
Talks.upsertReturning(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
}
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES ('intro-to-kotlin', 'Kotlin: A Practical Introduction', 15, TIMESTAMP '2026-09-01 11:00:00')
ON CONFLICT (slug) DO
UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at
    RETURNING *;
```

---
magic-move
---

# Upsert states the conflict key

```kotlin
Talks.upsertReturning(Talks.slug, returning = [Talks.id, Talks.updatedAt]) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
}
```

```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES ('intro-to-kotlin', 'Kotlin: A Practical Introduction', 15, TIMESTAMP '2026-09-01 11:00:00')
ON CONFLICT (slug) DO
UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at
    RETURNING id, updatedAt;
```

---

# Delete states its scope

<DrawnAnnotation text="removed" label="`deleteWhere` returns the affected row count"  :geometry="{ label: { x: 0.3653, y: 0.2844, width: 0.4222 } }" />

```kotlin
val removed: Int = Talks.deleteWhere { Talks.id eq talkId }
```

---
magic-move
---

# Delete states its scope

<DrawnAnnotation text="deleteReturning" label="Use `deleteReturning` to return affected rows"  />

```kotlin
val removed: Iterable<ResultRow> = Talks.deleteReturning { Talks.id eq talkId }
```

---
magic-move
---

# Delete states its scope

```kotlin
val removed: Iterable<TalkPreview> =
    Talks.deleteReturning { Talks.id eq talkId }
        .map { it.toTalkPreview() }
```

---

# Keep generated SQL visible

```kotlin
transaction(database) {
    addLogger(StdOutSqlLogger)
    Talks.select(Talks.title)
        .where { Talks.isPublished eq true }
        .toList()
}
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
