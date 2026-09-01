---
layout: intro
class: section-slide
kodee: wave
---

# Lesson 5 — DAO over `UuidTable`s

## An optional entity-oriented layer on the same meetup model

---

# DAO starts with the table you already have

> DAO is a mapping layer, not a second schema.

<DrawnAnnotation text="UuidEntity(id)" label="Ties Talk instance to the row `id: EntityID<Uuid>`" on="0" />
<DrawnAnnotation text="UuidEntityClass<Talk>(Talks)" label="Bind the entity manager to the `IdTable`" on="1" />

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)
}
```

---
magic-move
---

# DAO starts with the table you already have

<DrawnAnnotation text="by Talks.title" label="Kotlin Property Delegates reads, and writes the column" />

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var title by Talks.title
}
```

---
magic-move
---

# Entity properties delegate to table columns

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var title by Talks.title
  var slug by Talks.slug
  var description by Talks.description
  var startsAt by Talks.startsAt
  var isPublished by Talks.isPublished
}
```

---

# `EntityClass` manages the record lifecycle

```kotlin
transaction(database) {
  val talk = Talk.new {
    title = newTalk.title
    speaker = Profile.findById(newTalk.speakerId)!!
    host = Profile.findById(newTalk.hostId)!!
    isPublished = false
  }

  val talkOrNull = Talk.findById(talk.id.value)
  talkOrNull?.isPublished = true
  // talkOrNull?.delete()  
}
```

---

# Relations become navigable properties

<DrawnAnnotation text="Profile" label="Profile is `UuidEntity` for `ProfileTable`" on="0"  :geometry="{ label: { x: 0.7953, y: 0.3557, width: 0.2236 }, connector: { start: { x: 0.3266, y: 0.3628 }, end: { x: 0.6899, y: 0.3601 } } }"/>
<DrawnAnnotation text="referencedOn Talks.speakerId" label="Creates a Kotlin delegate that references `Profile` on `speakerId` as foreign key" on="1"  :geometry="{ label: { x: 0.6588, y: 0.4710, width: 0.4278 }, connector: { start: { x: 0.5913, y: 0.3719 }, end: { x: 0.5913, y: 0.4352 } } }"/>

```kotlin
class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
  companion object : UuidEntityClass<Talk>(Talks)

  var speaker by Profile referencedOn Talks.speakerId
  var host by Profile referencedOn Talks.hostId
  var tags by Tag via TalkTags
}
```

```kotlin
talk.speaker.name
talk.host.name
talk.tags.map(Tag::label)
```

---

# Navigable does not mean already loaded

> References are lazy by default.

```kotlin
val talks: Iterable<Talk> = Talk
  .find { Talks.isPublished eq true }
```

---

# Navigable does not mean already loaded

> Avoid discovering relation queries one entity at a time in a loop

<DrawnAnnotation text="talk.id" label="Every access results in a SELECT query so talks.size queries" />

```kotlin
val talks: Iterable<Talk> = Talk
  .find { Talks.isPublished eq true }

for(talk in talks) {
  println("Speaker: ${talk.speaker}, Host: ${talk.host}")
}
```

---

# Navigable does not mean already loaded

<DrawnAnnotation text="with" label="Eagerly load referenced properties in this collection"  :geometry="{ label: { x: 0.6038, y: 0.3640 }, connector: { start: { x: 0.1359, y: 0.3189 }, end: { x: 0.3532, y: 0.3603 } } }"/>

```kotlin
val talks = Talk
  .find { Talks.isPublished eq true }
  .with(Talk::speaker, Talk::host)

for(talk in talks) {
  println("Speaker: ${talk.speaker}, Host: ${talk.host}")
}
```

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
- `TalkPreview` → ordinary application data

> Model relationally, migrate deliberately, compose SQL visibly, and use DAO only when entity-oriented access earns its
> lifecycle cost.
