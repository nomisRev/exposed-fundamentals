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

> Generated SQL can include destructive changes.
>
> **Review it before it reaches a real database.**


```text
Kotlin table model → compare / generate → reviewed Flyway SQL → PostgreSQL
```

---

# Exposed Gradle Plugin

```kotlin gradle
plugins {
  id("org.jetbrains.exposed.plugin") version "1.5.0"
}
```

---
magic-move
---

# Exposed Gradle Plugin

```kotlin gradle
plugins {
  id("org.jetbrains.exposed.plugin") version "1.5.0"
}

exposed {
  migrations {
    // Optional configuration naming, fileDirectory, etc.
    // By default, follows Flyway standards
  }
}
```

<DrawnAnnotation text="generateMigrations" label="Writes migration files to src/main/resources/db/migration" at="1" passive />

<div v-click="1">

```bash
./gradlew generateMigrations
```

</div>

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

# Generation has three separate questions

| Question                     | Application-side answer   | Database-side answer                        |
|------------------------------|---------------------------|---------------------------------------------|
| Who computes the value?      | Kotlin code               | SQL function, default, sequence, or trigger |
| When can Kotlin know it?     | Before the insert         | From `RETURNING` or a later read            |
| Who applies it consistently? | Every writing application | Every writer that omits the column          |

---

# Exposed supports several default styles

<DrawnAnnotation text="defaultExpression(CurrentTimestamp)" label="CURRENT_TIMESTAMP" on="1"  :geometry="{ label: { x: 0.7102, y: 0.4024 } }"/>
<DrawnAnnotation text="databaseGenerated" label="Custom SQL i.e. PostgreSQL `TRIGGER`" on="2"  :geometry="{ label: { x: 0.3230, y: 0.6135 }, connector: { start: { x: 0.2020, y: 0.5629 }, end: { x: 0.2378, y: 0.5985 } } }"/>
<DrawnAnnotation text="databaseGenerated" label="Allows omitting value when inserting" on="3"/>

```kotlin
val appUpdatedAt = timestamp("app_updated_at")
  .clientDefault { Clock.System.now() }

val sqlUpdatedAt = timestamp("sql_updated_at")
  .defaultExpression(CurrentTimestamp)

val managedUpdatedAt = timestamp("managed_created_at")
  .databaseGenerated()
```

---

# Custom Function for default expression

<DrawnAnnotation text="defaultExpression(GenerateUuidV7)" label="DEFAULT uuidv7()" />
<DrawnAnnotation text="q { +&quot;uuidv7()&quot; }" label="Specialised DSL for building custom functions" on="1" />

```kotlin
object GenerateUuidV7 : Function<Uuid>(UuidColumnType()) {
  override fun toQueryBuilder(q: QueryBuilder) = q { +"uuidv7()" }
}

val id = uuid("id").defaultExpression(GenerateUuidV7)
```

---

# Read database-generated values deliberately

```kotlin
val created = Talks.insertReturning(
  listOf(Talks.id, Talks.createdAt, Talks.updatedAt),
) {
  it[title] = draft.title
}.single()
```

---

# All generated values have the same trade-off

> Choose one owner per rule.
>
> If a trigger owns `updated_at`, model that behaviour in Exposed and retrieve the final value when needed.

| Application-managed               | Database-managed                         |
|-----------------------------------|------------------------------------------|
| Use an explicit, injectable clock | Use `CURRENT_TIMESTAMP` for every writer |
| Know the value before writing     | Need `RETURNING` for the final value     |
| Every write path applies the rule | Defaults/triggers cover other writers    |
