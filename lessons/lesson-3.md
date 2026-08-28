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

````md sync-magic-move
```kotlin
Talks.selectAll()
```

```kotlin
Talks.select(Talks.id, Talks.title, Talks.startsAt)
```

```kotlin
Talks.select(id, title, startsAt)
```

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
```

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
```

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
```

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
    .offset(30)
```

```kotlin
Talks.select(id, title, startsAt)
    .where { id eq requestedTalkId }
    .orderBy(title, SortOrder.ASC)
    .limit(10)
    .offset(30)
    .andWhere { description like "%Kotlin%" }
```

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
````

````md sync-magic-move
```sql
SELECT *
FROM talks
```

```sql
SELECT id, title, startsAt
FROM talks
```

```sql
SELECT id, title, startsAt
FROM talks
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
LIMIT 10
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

```sql
SELECT id, title, startsAt
FROM talks
WHERE id = :requestedTalkId AND description LIKE '%Kotlin%'
ORDER BY title ASC
LIMIT 10
OFFSET 30
```

```sql
SELECT id, speakerId, title, description, startsAt
FROM talks
WHERE id = :requestedTalkId AND description LIKE '%Kotlin%'
ORDER BY title ASC
LIMIT 10
OFFSET 30
```
````

---

# SQL-shaped Kotlin

<DrawnAnnotation text="innerJoin" label="rightJoin, leftJoin, fullJoin, crossJoin" on="0" placement="up">

````md sync-magic-move
```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
```

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
```

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
    .where { Talks.isPublished eq true }
```
````

````md sync-magic-move
```sql
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id
```

```sql
SELECT talks.title, talks.description, profiles.name, profiles.avatar_url
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id

```

```sql
SELECT talks.title, talks.description, profiles.name, profiles.avatar_url
FROM talks
INNER JOIN profiles ON profiles.id = talks.speaker_id
WHERE talks.is_published = TRUE
```
````

</DrawnAnnotation>

---

# Query

<DrawnAnnotation text="Query" label="Lazy mutable builder allows building SQL" on="0">

```kotlin
fun previews(): Query =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { Talks.isPublished eq true }
```

</DrawnAnnotation>

---

# Executing the query

<DrawnAnnotation text="Blocking" label="JDBC is a blocking driver" on="0" placement="up" color="#f59e0b">

```kotlin jdbc
class Query(...) :
    SizedIterable<ResultRow>,
    BlockingExecutable<ResultApi, Query>
```

</DrawnAnnotation>



<DrawnAnnotation text="Suspend" label="R2DBC is a reactive driver"  on="0" color="#06b6d4">

```kotlin r2dbc
class Query(...) :
    SizedIterable<ResultRow>,
    SuspendExecutable<ResultApi, Query>
```

</DrawnAnnotation>


---

# Executing the query

<DrawnAnnotation text="Iterable" occurrence="2" label="Blocking cursor" placement="up" on="0" color="#f59e0b">

```kotlin jdbc
interface SizedIterable<out T> : Iterable<T> {
    fun limit(count: Int): SizedIterable<T>
    fun offset(start: Long): SizedIterable<T>
    fun count(): Long
    fun empty(): Boolean
}
```

</DrawnAnnotation>



<DrawnAnnotation text="Flow" on="0" color="#06b6d4">
<DrawnAnnotation text="suspend" on="0" color="#06b6d4">
<DrawnAnnotation text="suspend" occurrence="2" label="R2DBC suspends like reactive types"  on="0" color="#06b6d4">

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

<DrawnAnnotation text="jdbc" on="0">
<DrawnAnnotation text="r2dbc" label="R2DBC and JDBC live alongside each other" on="0">

```kotlin jdbc
package org.jetbrains.exposed.v1.jdbc

class Query(...) : Iterable<ResultRow>
```

```kotlin r2dbc
package org.jetbrains.exposed.v1.r2dbc

class Query(...) : Flow<ResultRow>
```

</DrawnAnnotation>
</DrawnAnnotation>

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

````md magic-move
```kotlin
fun previews(): Query =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { isPublished eq true }
```

```kotlin
fun previews(): Iterable<ResultRow> =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { isPublished eq true }
```

```kotlin
fun previews(): List<?> =
    Talks.innerJoin(ProfileTable) { speakerId eq ProfileTable.id }
        .select(title, description, ProfileTable.name, ProfileTable.avatarUrl)
        .where { isPublished eq true }
        .map { row: ResultRow -> TODO("Return value") }
```

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
````

---

# Accessing the data

<DrawnAnnotation text="Column<A>): A" on="0" label="Typed data retrieval from ResultRow">
<DrawnAnnotation text="title = " on="1" label="String">
<DrawnAnnotation text="Talks.title" on="1" label="Column<String>">
<DrawnAnnotation text="operator" on="2">
<DrawnAnnotation text="[Talks.title]" on="2" label="Kotlin index access operator">

````md magic-move
```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview(): TalkPreview
```

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
    title = this.get(Talks.title),
) 
```

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
    title = this[Talks.title],
) 
```

```kotlin
operator fun <A> ResultRow.get(key: Column<A>): A = TODO("Exposed internals")

fun ResultRow.toTalkPreview() = TalkPreview(
    title = this[Talks.title],
    description = this[Talks.description],
    speakerName = this[ProfileTable.name],
    speakerAvatarUrl = this[ProfileTable.avatarUrl],
) 
```
````

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>


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

<DrawnAnnotation text="previews" label="Can be called from anywhere resulting in java.lang.IllegalStateException: No transaction in context." color="red" on="0">
<DrawnAnnotation text="context(_: Transaction)" label="Compile-time guarantee it's called inside a Transaction" on="1">
<InlineCompilerError text="previews" occurrence="2" message="No context argument for '_: Transaction' found" at="2">
<DrawnAnnotation text="Transaction" occurrence="2" label="Transaction context argument" at="3">

````md magic-move
```kotlin
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }
```

```kotlin
context(_: Transaction)
fun previews(): List<TalkPreview> =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.id, Talks.title, Talks.slug, Talks.startAt, ProfileTable.name)
        .map { it.toTalkPreview() }

fun example() = previews()
```

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
````

<v-clicks>
<DrawnAnnotation text="Please call Database.connect() first or specify a database explicitly in the transaction call">

<div class="stacktrace" aria-label="Application stack trace">
  <div>Exception in thread "main" <span class="stacktrace-link">java.lang.IllegalStateException</span>: No database specified and no default database found.<br />Please call Database.connect() first or specify a database explicitly in the transaction call.</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.resolveDatabaseOrThrow(<span class="stacktrace-link">Transactions.kt:108</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.transaction(<span class="stacktrace-link">Transactions.kt:139</span>)</div>
  <div>&nbsp;&nbsp;&nbsp;&nbsp;at org.jetbrains.exposed.v1.jdbc.transactions.TransactionsKt.transaction$default(<span class="stacktrace-link">Transactions.kt:133</span>)</div>
</div>

</DrawnAnnotation>
</v-clicks>

</DrawnAnnotation>
</InlineCompilerError>
</DrawnAnnotation>
</DrawnAnnotation>

---

# Database work requires a transaction

<DrawnAnnotation text="(database)" label="explicitly specified database in the transaction call" on="0">
<DrawnAnnotation text="(database: Database)" label="But where is this coming from?" on="1">

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

<DrawnAnnotation text="transaction {" label="Default database found (from Database.connect)" on="0">
<DrawnAnnotation text="transaction(database)" label="Prefer explicitly passing database instead of relying on implicit resolution" on="0">

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

<DrawnAnnotation text="@Transactional" label="Spring's @Transactional can also provide Exposed Transaction" on="0">

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

<DrawnAnnotation text="insert" occurrence="2" label="InsertStatement.() -> Unit builder" on="0">
<DrawnAnnotation text="operator" on="1" color="pink">
<DrawnAnnotation text="set" on="1"color="pink">
<DrawnAnnotation text="it[" label="Operator set with Column" on="1" color="pink">
<DrawnAnnotation text="] =" on="1" color="pink">
<DrawnAnnotation text="Column<S>" on="1">
<DrawnAnnotation text="value: S" on="1">
<DrawnAnnotation text="Talks.title" on="1">
<DrawnAnnotation text="title" occurrence="3" label="Typesafe setter based on Column<S> type" on="1">
<DrawnAnnotation text="InsertStatement" label="The executed insert statement, providing the affected-row count and any available generated values" on="2">

````md magic-move
```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {

    }
```

```kotlin
operator fun <S> InsertStatement.set(column: Column<S>, value: S) = TODO("")

context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant) =
    Talks.insert {
        it[Talks.title] = title
    }
```

```kotlin
context(_: Transaction)
fun insert(title: String, speakerId: Long, startsAt: Instant): InsertStatement<Number> =
    Talks.insert {
        it[Talks.title] = title
        it[Talks.speakerId] = speakerId
        it[Talks.startsAt] = startsAt
    }
```
````

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>

---

# Insert and get id

<DrawnAnnotation text="EntityID<Long>" label="Returns EntityID not raw Long" on="0">
<DrawnAnnotation text=".value" label="Explicitly unwrap" on="1">

````md magic-move
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
````

</DrawnAnnotation>
</DrawnAnnotation>

---

# Insert a talk — return what you need

<DrawnAnnotation text="listOf(Talks.id, Talks.createdAt, Talks.updatedAt)" label="Be explicit about returned data" on="0">
<DrawnAnnotation text="[Talks.id, Talks.createdAt, Talks.updatedAt]" label="Use collection literals if you do!" on="1">
<DrawnAnnotation text="insertReturning {" label="Or RETURNING *" on="2">
<DrawnAnnotation text="TalkWithSpeakerId" on="3">
<DrawnAnnotation text="map(ResultRow::toTalkWithSpeakerId)" label="Return all data typed whilst inserting" on="3" placement="down">

````md magic-move
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
````

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>

---

# Insert a talk — do nothing if exists

````md sync-magic-move
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
````

---

# Insert collections with `batchInsert`

````md magic-move
```kotlin
context(_: Transaction)
fun insertBatch(newTalk: List<NewTalk>) =
    Talks.batchInsert(newTalk) { newTalk ->
        this[Talks.title] = newTalk.title
        this[Talks.speakerId] = newTalk.speakerId
        this[Talks.startsAt] = newTalk.startsAt
    }.map(ResultRow::toTalkWithoutSpeaker)
```

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
````

---

# Update states its scope

<DrawnAnnotation text="{ Talks.id eq talkId }" label="Without a precise predicate can affect many rows." on="0">
<DrawnAnnotation text="it[Talks.status] = TalkStatus.PUBLISHED" label="Same DSL as insert" on="1">
<DrawnAnnotation text="[Talks.speakerId, Talks.title, Talks.startsAt]" label="Use Returning variant to return selected columns" on="2">
<DrawnAnnotation text="{ Talks.id eq talkId }" on="3">
<DrawnAnnotation text="singleOrNull()" label="Predicate guarantees 1 or 0 rows will be affected" on="3">

````md magic-move
```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```

```kotlin
fun update(talkId: Long, status: TalkStatus) {
    Talks.update({ Talks.id eq talkId }) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }
}
```

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```

```kotlin
fun update(talkId: Long, status: TalkStatus): TalkPreview? =
    Talks.updateReturning(
        [Talks.speakerId, Talks.title, Talks.startsAt],
        { Talks.id eq talkId }
    ) {
        it[Talks.status] = TalkStatus.PUBLISHED
    }.singleOrNull()?.toTalkPreview()
```
````

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>

---

# Upsert states the conflict key

<DrawnAnnotation text="Talks.slug" label="Specify unique key on which to update the row on conflict" on="1">
<DrawnAnnotation text="(slug)" label="Must have UNIQUE constraint" on="1">

````md sync-magic-move
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
ON CONFLICT (slug)
DO UPDATE SET
    title = EXCLUDED.title,
    speaker_id = EXCLUDED.speaker_id,
    starts_at = EXCLUDED.starts_at;
```

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
````

</DrawnAnnotation>
</DrawnAnnotation>

---

# Delete states its scope

<DrawnAnnotation text="removed" label="deleteWhere` returns the affected row count" on="0">
<DrawnAnnotation text="deleteReturning" label="Use deleteReturning to return affected rows" on="1">

````md magic-move
```kotlin
val removed: Int = Talks.deleteWhere { Talks.id eq talkId }
```

```kotlin
val removed: Iterable<ResultRow> = Talks.deleteReturning { Talks.id eq talkId }
```

```kotlin
val removed: Iterable<TalkPreview> =
    Talks.deleteReturning { Talks.id eq talkId }
        .map { it.toTalkPreview() }
```
````

</DrawnAnnotation>
</DrawnAnnotation>


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
