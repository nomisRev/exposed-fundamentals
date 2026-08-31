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
Talks.select(id, title, startsAt)
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
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
LIMIT 10
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
    .offset(30)
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
    .offset(30)
    .andWhere { description like "%Kotlin%" }
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId AND description LIKE '%Kotlin%'
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

---
magic-move
---

# SQL-shaped Kotlin

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
    .offset(30)
    .andWhere { description like "%Kotlin%" }
    .adjustSelect {
        select(id, speakerId, title, description, startsAt)
    }
```

```sql
SELECT id, speakerId, title, description, startsAt
FROM talks
WHERE id = :requestedTalkId AND description LIKE '%Kotlin%'
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

---

# SQL-shaped Kotlin

<DrawnAnnotation text="innerJoin" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="INNER JOIN" label="rightJoin, leftJoin, fullJoin, crossJoin" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.2644, y: 0.4239 }, connector: { type: 'polyline', points: [{ x: 0.1153, y: 0.3457 }, { x: 0.1324, y: 0.3905 }] } }">

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
```
```sql
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id
```

</DrawnAnnotation>
</DrawnAnnotation>

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

<DrawnAnnotation text="Query" label="Lazy mutable builder allows building SQL" :geometry="{ label: { x: 0.5240, y: 0.1091 }, connector: { type: 'quadratic', start: { x: 0.2712, y: 0.2178 }, control: { x: 0.3286, y: 0.1941 }, end: { x: 0.3609, y: 0.1519 } } }">

```kotlin
fun previews(): Query =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { Talks.isPublished eq true }
```

</DrawnAnnotation>

---

# Executing the query

<DrawnAnnotation text="Blocking" label="JDBC is a blocking driver" placement="up" color="#f59e0b"  :geometry="{ label: { x: 0.5845, y: 0.2111 }, connector: { type: 'quadratic', start: { x: 0.1853, y: 0.3019 }, control: { x: 0.5448, y: 0.3080 }, end: { x: 0.5757, y: 0.2500 } } }">

```kotlin jdbc
class Query(...) :
    SizedIterable<ResultRow>,
    BlockingExecutable<ResultApi, Query>
```
</DrawnAnnotation>



<DrawnAnnotation text="Suspend" label="R2DBC is a reactive driver" color="#06b6d4"  :geometry="{ label: { x: 0.2150, y: 0.5444 } }">

```kotlin r2dbc
class Query(...) :
    SizedIterable<ResultRow>,
    SuspendExecutable<ResultApi, Query>
```
</DrawnAnnotation>



---

# Executing the query

<DrawnAnnotation text="Iterable" occurrence=2 label="Blocking cursor" placement="up" color="#f59e0b"  :geometry="{ label: { x: 0.5473, y: 0.1190 }, connector: { type: 'polyline', points: [{ x: 0.4427, y: 0.1752 }, { x: 0.4750, y: 0.1193 }] } }">

```kotlin jdbc
interface SizedIterable<out T> : Iterable<T> {
    fun limit(count: Int): SizedIterable<T>
    fun offset(start: Long): SizedIterable<T>
    fun count(): Long
    fun empty(): Boolean
}
```
</DrawnAnnotation>



<DrawnAnnotation text="Flow" color="#06b6d4" >
<DrawnAnnotation text="suspend" color="#06b6d4" >
<DrawnAnnotation text="suspend" occurrence=2 label="R2DBC suspends like reactive types" color="#06b6d4"  :geometry="{ label: { x: 0.2388, y: 0.7998 } }">

```kotlin r2dbc
interface SizedIterable<out T> : Flow<T> {
    fun limit(count: Int): SizedIterable<T>
    fun offset(start: Long): SizedIterable<T>
    suspend fun count(): Long
    suspend fun empty(): Boolean
}
```
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>



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

<DrawnAnnotation text="jdbc" >
<DrawnAnnotation text="r2dbc" label="R2DBC and JDBC live alongside each other" >

```kotlin jdbc
package org.jetbrains.exposed.v1.jdbc

class Query(...) : Iterable<ResultRow>
```
</DrawnAnnotation>
</DrawnAnnotation>


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
fun talks(requestedTalkId: Long, limit: Long = 10, offset: Long = 30): Query =
    Talks.select(id, speakerId, title, description, startsAt)
        .where { id eq requestedTalkId }
        .andWhere { description like "%Kotlin%" }
        .orderBy(title, SortOrder.ASC)
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

<DrawnAnnotation text="Column<A>): A" label="Typed data retrieval from ResultRow" >

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview(): TalkPreview
```
</DrawnAnnotation>


---
magic-move
---

# Accessing the data

<DrawnAnnotation text="title = " label="String" >
<DrawnAnnotation text="Talks.title" label="Column<String>" >

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
    title = this.get(Talks.title),
) 
```
</DrawnAnnotation>
</DrawnAnnotation>


---
magic-move
---

# Accessing the data

<DrawnAnnotation text="operator" >
<DrawnAnnotation text="[Talks.title]" label="Kotlin index access operator" >

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
    title = this[Talks.title],
) 
```
</DrawnAnnotation>
</DrawnAnnotation>


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

---

# Accessing the data

```kotlin
fun ResultRow.toTalkPreview(): TalkPreview

val results = Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
    .map { it.toTalkPreview() }
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

<DrawnAnnotation text="previews" label="Can be called from anywhere resulting in java.lang.IllegalStateException: No transaction in context." color="red" >

```kotlin
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```
</DrawnAnnotation>


---
magic-move
---

# Database work requires a transaction

<DrawnAnnotation text="context(_: Transaction)" label="Compile-time guarantee it's called inside a Transaction" >

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```
</DrawnAnnotation>


---
magic-move
---

# Database work requires a transaction

<InlineCompilerError text="previews" occurrence=2 message="No context argument for '_: Transaction' found" on="0">

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

<InlineCompilerError text="previews" occurrence=2 message="No context argument for '_: Transaction' found" on="0">

<DrawnAnnotation text="Transaction" occurrence=2 label="Transaction context argument" >
<DrawnAnnotation text="Please call Database.connect() first or specify a database explicitly in the transaction call" at="1" >

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
</DrawnAnnotation>
</DrawnAnnotation>


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

<DrawnAnnotation text="(database)" label="explicitly specified database in the transaction call" >
<DrawnAnnotation text="(database: Database)" label="But where is this coming from?" on="1" >

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }

fun example(database: Database) = transaction(database) { previews() }
```
</DrawnAnnotation>
</DrawnAnnotation>


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

<DrawnAnnotation text="transaction {" label="Default database found (from Database.connect)" >
<DrawnAnnotation text="transaction(database)" label="Prefer explicitly passing database instead of relying on implicit resolution" >

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }

fun example(database: Database) = transaction(database) { previews() }

fun example() = transaction { previews() }
```
</DrawnAnnotation>
</DrawnAnnotation>


---

# Database work requires a transaction

<DrawnAnnotation text="@Transactional" label="Spring's @Transactional can also provide Exposed Transaction" >

```kotlin
@Transactional
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```
</DrawnAnnotation>


---

# Insert a talk

<DrawnAnnotation text="insert" occurrence=2 label="InsertStatement.() -> Unit builder" >

```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {

    }
```
</DrawnAnnotation>


---

# Insert a talk

<DrawnAnnotation text="operator" color="pink" >
<DrawnAnnotation text="set" color="pink" >
<DrawnAnnotation text="it[" label="Operator set with Column" color="pink" >
<DrawnAnnotation text="] =" color="pink" >
<DrawnAnnotation text="Column<S>" >
<DrawnAnnotation text="value: S" >
<DrawnAnnotation text="Talks.title" >
<DrawnAnnotation text="title" occurrence=3 label="Typesafe setter based on Column<S> type" >

```kotlin
operator fun <S> InsertStatement.set(column: Column<S>, value: S) = TODO("")

context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {
        it[Talks.title] = title
    }
```
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>


---

# Insert a talk

<DrawnAnnotation text="InsertStatement" label="The executed insert statement, providing the affected-row count and any available generated values" >

```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant): InsertStatement<Number> =
    Talks.insert {
        it[Talks.title] = title
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }
```
</DrawnAnnotation>


---

# Insert and get id

<DrawnAnnotation text="EntityID<Long>" label="Returns EntityID not raw Long" >

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
</DrawnAnnotation>


---
magic-move
---

# Insert and get id

<DrawnAnnotation text=".value" label="Explicitly unwrap" >

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
</DrawnAnnotation>


---

# Insert a talk — return what you need

<DrawnAnnotation text="listOf(Talks.id, Talks.createdAt, Talks.updatedAt)" label="Be explicit about returned data" >

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
</DrawnAnnotation>


---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="[Talks.id, Talks.createdAt, Talks.updatedAt]" label="Use collection literals if you do!" >

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
</DrawnAnnotation>


---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="insertReturning {" label="Or RETURNING *" >

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
</DrawnAnnotation>


---
magic-move
---

# Insert a talk — return what you need

<DrawnAnnotation text="TalkWithSpeakerId" >
<DrawnAnnotation text="map(ResultRow::toTalkWithSpeakerId)" label="Return all data typed whilst inserting" placement="down" >

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
</DrawnAnnotation>
</DrawnAnnotation>


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

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Without a precise predicate can affect many rows." >

```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```
</DrawnAnnotation>


---
magic-move
---

# Update states its scope

<DrawnAnnotation text="it[Talks.status] = TalkStatus.PUBLISHED" label="Same DSL as insert" >

```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```
</DrawnAnnotation>


---
magic-move
---

# Update states its scope

<DrawnAnnotation text="[Talks.speakerId, Talks.title, Talks.startsAt]" label="Use Returning variant to return selected columns" >

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```
</DrawnAnnotation>


---
magic-move
---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" >
<DrawnAnnotation text="singleOrNull()" label="Predicate guarantees 1 or 0 rows will be affected" >

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```
</DrawnAnnotation>
</DrawnAnnotation>


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
ON CONFLICT
DO UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at;
```

---
magic-move
---

# Upsert states the conflict key

<DrawnAnnotation text="Talks.slug" label="Specify unique key on which to update the row on conflict" >
<DrawnAnnotation text="(slug)" label="Must have UNIQUE constraint" >

```kotlin
Talks.upsert(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
}
```
</DrawnAnnotation>
</DrawnAnnotation>


```sql
INSERT INTO talks (slug, title, speaker_id, starts_at)
VALUES ('intro-to-kotlin', 'Kotlin: A Practical Introduction', 15, TIMESTAMP '2026-09-01 11:00:00')
ON CONFLICT (slug)
DO UPDATE SET
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
ON CONFLICT (slug)
DO UPDATE SET
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
ON CONFLICT (slug)
DO UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at
RETURNING id, updatedAt;
```

---

# Delete states its scope

<DrawnAnnotation text="removed" label="deleteWhere` returns the affected row count" >

```kotlin
val removed: Int = Talks.deleteWhere { Talks.id eq talkId }
```
</DrawnAnnotation>


---
magic-move
---

# Delete states its scope

<DrawnAnnotation text="deleteReturning" label="Use deleteReturning to return affected rows" >

```kotlin
val removed: Iterable<ResultRow> = Talks.deleteReturning { Talks.id eq talkId }
```
</DrawnAnnotation>


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
