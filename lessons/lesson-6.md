---
layout: intro
class: section-slide
kodee: wave
---

<div class="lesson-number">Topic 4</div>

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

