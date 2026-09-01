---
layout: intro
class: section-slide
kodee: wave
---

# Lesson 5 — DAO over `UuidTable`s

## An optional entity-oriented layer on the same relational model

---

# DAO starts with the table you already have

```kotlin
object Talks : UuidTable("talks") {
  val speakerId = reference("speaker_id", ProfileTable)
  val hostId = reference("host_id", ProfileTable)
  val title = varchar("title", 200)
}

class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var title by Talks.title
}
```

`UuidTable` → `UuidEntity` → `UuidEntityClass`

> DAO is a mapping layer, not a second schema.

---

# Entity properties delegate to table columns

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var title by Talks.title
  var published by Talks.isPublished
}
```

- `EntityID<Uuid>` is Exposed’s record ID wrapper.
- `UuidEntityClass<Talk>(Talks)` binds the manager to its `IdTable`.
- `by Talks.title` reads and writes the existing table column.

---

# `EntityClass` manages the record lifecycle

```kotlin
transaction(database) {
  val talk = Talk.new {
    title = draft.title
    speaker = speakerEntity
    host = hostEntity
    published = false
  }

  Talk.findById(talk.id.value)?.let {
    it.published = true
    // it.delete()
  }
}
```

DAO does not remove the transaction boundary. It changes the access style inside it.

---

# Relations become navigable properties

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var speaker by Person referencedOn Talks.speakerId
  var host by Person referencedOn Talks.hostId
  var tags by Tag via TalkTags
}
```

```kotlin
talk.speaker.name
talk.host.name
talk.tags.map(Tag::label)
```

The delegates use the same foreign keys and join table as the SQL DSL.

---

# Navigable does not mean already loaded

```kotlin
val talks = Talk
  .find { Talks.isPublished eq true }
  .with(Talk::speaker, Talk::host, Talk::tags)
```

- References are lazy by default.
- The first relation access may query.
- Use `with(...)` when a collection’s known relations are needed.

> Avoid discovering relation queries one entity at a time in a loop: the N+1 problem.

---

# An entity belongs to its transaction

```text
transaction(database)
  └─ entity cache
      └─ Talk(id) → one managed instance

application boundary
  └─ TalkPreview(...) → ordinary detached data
```

- DAO has identity and caching within a transaction.
- Lazy access after the transaction is a lifecycle bug.
- Return DTOs, not entities, across service and API boundaries.

---

# Mix APIs at a deliberate boundary

```kotlin
val query = Talks
  .select(Talks.columns)
  .where { Talks.isPublished eq true }

val talks = Talk.wrapRows(query)
  .with(Talk::speaker, Talk::host, Talk::tags)
```

- `wrapRows` turns entity-table rows into managed entities.
- Keep them inside the transaction.
- Load the relations the caller needs.

---

# Choose the access style for the job

| Prefer SQL DSL                          | Consider DAO                         |
|-----------------------------------------|--------------------------------------|
| Projections and DTO responses           | Record-oriented CRUD                 |
| Reporting, aggregates, window functions | Navigable relationships              |
| Complex joins, aliases, subqueries      | Transaction-scoped entity cache      |
| Explicit SQL-shaped query control       | Carefully managed lazy/eager loading |

> Neither API replaces relational design. Choose the one that makes the operation’s shape and lifecycle clearest.

---

# DAO is optional — the foundation stays shared

- `UuidTable` → shared columns, keys, constraints
- `UuidEntity` → delegated properties and relations
- `transaction + cache` → lifecycle context
- `DTO boundary` → ordinary application data

> Model relationally, migrate deliberately, compose SQL visibly, and use DAO only when entity-oriented access earns its
> lifecycle cost.
