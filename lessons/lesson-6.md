---
layout: intro
class: section-slide
kodee: wave
---

<div class="lesson-number">Lesson 2</div>

# Schema ownership

---

# Two models, one production schema

<div class="schema-ownership-visual" aria-label="Exposed models a PostgreSQL schema while Flyway owns its production evolution">
  <section class="schema-model schema-exposed">
    <header class="schema-model-heading">
      <picture class="schema-kotlin-logo" aria-hidden="true">
        <source media="(prefers-color-scheme: dark)" srcset="/kotlin_dark.svg">
        <img src="/kotlin.svg" alt="">
      </picture>
      <img class="schema-exposed-logo" src="/Exposed icon.svg" alt="" aria-hidden="true">
      <span>Kotlin + Exposed</span>
    </header>
    <p class="schema-model-kicker">Application model</p>
    <ul>
      <li>Types and columns</li>
      <li>Keys and constraints</li>
      <li>Typed queries</li>
    </ul>
    <strong class="schema-model-role">Models the database</strong>
  </section>

  <div class="schema-relationship" aria-hidden="true">
    <span class="schema-relationship-line"></span>
    <strong>same schema</strong>
    <span class="schema-relationship-line"></span>
  </div>

  <section class="schema-model schema-flyway">
    <header class="schema-model-heading">
      <img class="schema-flyway-logo" src="/flyway-seeklogo.svg" alt="" aria-hidden="true">
      <img class="schema-postgres-logo" src="/Postgresql_elephant.svg" alt="" aria-hidden="true">
      <span>Flyway + PostgreSQL</span>
    </header>
    <p class="schema-model-kicker">Production schema</p>
    <ul>
      <li>Reviewed SQL changes</li>
      <li>Versioned migration history</li>
      <li>Database enforcement</li>
    </ul>
    <strong class="schema-model-role">Owns schema evolution</strong>
  </section>
</div>

---

# A migration history is a story

> Each file explains **what changed**, is reviewed in Git
>
> History makes deployments repeatable

1. `V1_1__create_tables.sql`
2. `V1_2__create_talks.sql`
3. `V1_3__create_tags_and_talk_tags.sql`
4. `V1_4__add_audit_trigger.sql`
5. `V2_1__alter_profiles_tables.sql`

---

# Migration files own the DDL

```sql
CREATE TABLE profiles
(
  id    UUID PRIMARY KEY,
  name  TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE
);
```

---

# Exposed can assist — it does not deploy

```text
Kotlin table model → compare / generate → reviewed Flyway SQL → PostgreSQL
```

> Generated SQL can include destructive changes.
> 
> **Review it before it reaches a real database.**

---

# Exposed Gradle Plugin

```kotlin gradle
plugins {
  id("org.jetbrains.exposed.plugin") version "1.4.0"
}
```

---
magic-move
---

# Exposed Gradle Plugin

```kotlin gradle
plugins {
  id("org.jetbrains.exposed.plugin") version "1.4.0"
}

exposed {
  migrations {
    // Optional configuration naming, fileDirectory, etc.
    // By default, follows Flyway standards
  }
}
```

<v-clicks at="1">
<DrawnAnnotation text="generateMigrations" label="Writes migration files to src/main/resources/db/migration" on="1" />

```bash
./gradlew generateMigrations
```

</v-clicks>

---

# Flyway

```kotlin gradle
buildscript {
  repositories { mavenCentral() }
  dependencies { classpath("org.flywaydb:flyway-database-postgresql:13.3.0") }
}

plugins { alias(libs.plugins.flyway) }

flyway {
  url = "jdbc:postgresql://localhost:5432/postgres"
  user = "postgres"
  password = ""
  baselineOnMigrate = true
  validateOnMigrate = true
}
```

```bash
./gradlew flywayMigrate
```

---

# Flyway

<DrawnAnnotation text="migrate" label="Idempotent operation so we can execute on server start-up"  :geometry="{ label: { x: 0.3537, y: 0.6057 }, connector: { start: { x: 0.0688, y: 0.2224 }, end: { x: 0.0724, y: 0.5713 } } }"/>

```kotlin
fun migrate(dataSource: HikariDataSource): MigrateResult =
  Flyway.configure()
    .dataSource(dataSource)
    .baselineOnMigrate(true)
    .validateOnMigrate(true)
    .load()
    .migrate()
```

---

# Each artifact has a different job

| Artifact          | Authority                                     |
|-------------------|-----------------------------------------------|
| Flyway history    | Which reviewed changes run, and in what order |
| PostgreSQL schema | What the database enforces now                |
| Exposed tables    | How Kotlin maps to and queries that schema    |

---

# Model the constraints that queries rely on

```kotlin
object Bookmarks : Table("bookmarks") {
  val personId = reference("person_id", ProfileTable, onDelete = ReferenceOption.CASCADE)
  val talkId = reference("talk_id", Talks, onDelete = ReferenceOption.CASCADE)
  val note = varchar("note", 300).nullable()

  override val primaryKey = PrimaryKey(personId, talkId)

  init {
    index("bookmarks_talk_idx", false, talkId)
  }
}
```

Migration SQL remains the production DDL. Mirror the constraints that Kotlin queries depend on.

---

# Generation has three separate questions

| Question                     | Application-side answer   | Database-side answer                        |
|------------------------------|---------------------------|---------------------------------------------|
| Who computes the value?      | Kotlin code               | SQL function, default, sequence, or trigger |
| When can Kotlin know it?     | Before the insert         | From `RETURNING` or a later read            |
| Who applies it consistently? | Every writing application | Every writer that omits the column          |

> “Generated by the database” is not automatically better, and “generated by the application” is not automatically
> simpler. Choose per value and system boundary.

---

# Exposed supports several default styles

```kotlin
val appCreatedAt = timestamp("app_created_at")
  .clientDefault { Clock.System.now() }

val sqlCreatedAt = timestamp("sql_created_at")
  .defaultExpression(CurrentTimestamp)

val managedCreatedAt = timestamp("managed_created_at")
  .databaseGenerated()
```

- `clientDefault` — Kotlin computes the value.
- `defaultExpression` — Exposed places a SQL expression in the statement.
- `databaseGenerated` — the insert may omit a DDL- or trigger-managed column.

---

# UUID v7 is one useful ID shape

- Time-sortable, with better index locality than random v4
- Globally unique without coordinating a sequence
- Can be generated before or during an insert
- Reveals approximate creation order and time
- Wider than integer keys and indexes

> UUID v7 is a trade-off, not a mandatory replacement for every primary key.

---

# Tables keep the relational model visible

```kotlin
object ProfileTable : UuidTable("profiles") {
  val name = varchar("name", 120)
}

object Talks : UuidTable("talks") {
  val speakerId = reference("speaker_id", ProfileTable)
  val hostId = reference("host_id", ProfileTable)
  val title = varchar("title", 200)
}
```

- `UuidTable` is an `IdTable` with a Kotlin `Uuid` key.
- `reference()` makes foreign keys explicit.
- `varchar()` keeps type and length in the model.

---

# Kotlin UUIDs and Exposed UUID tables

- Kotlin 2.4: `kotlin.uuid.Uuid` is stable.
- Kotlin 2.4: `Uuid.generateV7()` is experimental (`@ExperimentalUuidApi`).

```kotlin
object Talks : UuidTable(
  name = "talks",
  uuidVersion = UuidVersion.V7,
) {
  val title = varchar("title", 200)
}
```

> `UuidTable` generates v4 by default; `UuidVersion.V7` opts into application-generated v7.

---

# Compare ID strategies

| Strategy      | Kotlin knows ID | Coordination / coupling | Cost                    |
|---------------|-----------------|-------------------------|-------------------------|
| DB sequence   | After insert    | Central sequence        | Compact; round-trip     |
| App UUID v4   | Before insert   | None                    | Random; 128-bit         |
| App UUID v7   | Before insert   | UUID algorithm          | Ordered; leaks time     |
| DB `uuidv7()` | After insert    | PostgreSQL / extension  | Default for all writers |

Consider offline creation, multiple writers, portability, key size, and when the ID is needed.

---

# PostgreSQL 18 can provide the default

```sql
CREATE TABLE talks
(
  id    UUID PRIMARY KEY DEFAULT uuidv7(),
  title TEXT NOT NULL
);
```

```kotlin
object Talks : Table("talks") {
  val id = uuid("id").databaseGenerated()
  val title = varchar("title", 200)
  override val primaryKey = PrimaryKey(id)
}
```

PostgreSQL 17 and older need an extension or custom function; `uuidv7()` is not portable PostgreSQL syntax.

---

# Or Exposed can request a database expression

```kotlin
object GenerateUuidV7 : Function<Uuid>(UuidColumnType()) {
  override fun toQueryBuilder(q: QueryBuilder) = q { +"uuidv7()" }
}

val id = uuid("id").defaultExpression(GenerateUuidV7)
```

- PostgreSQL computes the value.
- Exposed places the expression in its statement.
- DDL `DEFAULT uuidv7()` also covers other clients that omit `id`.

> Computation location and default ownership are separate choices.

---

# Read database-generated values deliberately

```kotlin
val created = Talks.insertReturning(
  listOf(Talks.id, Talks.createdAt),
) {
  it[title] = draft.title
}.single()
```

- `databaseGenerated()` lets an insert omit the column.
- `insertReturning` retrieves the final value immediately.
- After a trigger runs, use `updateReturning` where supported or re-read.

---

# Timestamps have the same trade-off

| Application-managed               | Database-managed                         |
|-----------------------------------|------------------------------------------|
| Use an explicit, injectable clock | Use `CURRENT_TIMESTAMP` for every writer |
| Know the value before writing     | May need `RETURNING` for the final value |
| Every write path applies the rule | Defaults/triggers cover other writers    |

Choose one owner per rule. If a trigger owns `updated_at`, model that behaviour in Exposed and retrieve the final value
when needed.
