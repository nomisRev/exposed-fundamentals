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

<EntityTransactionBoundary />

- **DAO has identity and caching** within a transaction.
- **Lazy access after the transaction** is a lifecycle bug.
- **Return DTOs, not entities** across service and API boundaries.

---

# Mix APIs at a deliberate boundary

<DrawnAnnotation text="wrapRows" label="Turns Query into managed entities allowing more complex relationships"  :geometry="{ label: { x: 0.4769, y: 0.5324, width: 0.4817 }, connector: { start: { x: 0.4369, y: 0.4238 }, end: { x: 0.4369, y: 0.5052 } } }"/>

```kotlin
val query = Talks
  .select(Talks.columns)
  .where { Talks.isPublished eq true }

val talks: Iterable<Talk> = Talk.wrapRows(query)
  .with(Talk::speaker, Talk::host, Talk::tags)
```

---

# DAO is optional — the foundation stays shared

- `UuidTable` → shared columns, keys, constraints
- `UuidEntity` → delegated properties and relations
- `transaction + cache` → lifecycle context
- `TalkPreview` → ordinary application data

---

# Generation has three separate questions

| Question                    | Application-side   | Database-side                              |
|-----------------------------|--------------------------|--------------------------------------------|
| Who computes?               | Kotlin code              | function, default, sequence, or trigger    |
| When can we read?           | Before the insert        | From `RETURNING` or a later read           |
| Who applies it? | Every writing application | Every writer that omits the column         |

---

# Exposed supports several default styles

<DrawnAnnotation text="clientDefault" label="Sent with every INSERT, so an upsert conflict overwrites it unless listed in `onUpdateExclude`" on="0" :geometry="{ label: { x: 0.72, y: 0.29, width: 0.44 }, connector: { start: { x: 0.23, y: 0.265 }, end: { x: 0.50, y: 0.29 } } }" />
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

