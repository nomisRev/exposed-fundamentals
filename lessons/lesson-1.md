---
layout: intro
class: section-slide
kodee: welcome
---

<div class="lesson-number">Topic 1</div>

# What is Exposed?

## Kotlin syntax, relational thinking

---

# The Kotlin ↔ database boundary

> Exposed connects the two.

<div class="boundary-visual" aria-label="Exposed makes the boundary between Kotlin and a relational database explicit">
  <section class="boundary-world boundary-kotlin">
    <div class="boundary-world-heading">
      <picture class="boundary-kotlin-logo" aria-hidden="true">
        <source media="(prefers-color-scheme: dark)" srcset="/kotlin_dark.svg">
        <img src="/kotlin.svg" alt="">
      </picture>
      <span>Kotlin</span>
    </div>
    <ul class="boundary-concepts">
      <li>Rich types</li>
      <li>Domain rules</li>
      <li>Application code</li>
    </ul>
  </section>

  <section class="boundary-crossing">
    <div class="boundary-arrow boundary-arrow-top" aria-hidden="true"></div>
    <div class="boundary-exposed-mark">
      <img class="boundary-exposed-logo" src="/Exposed icon.svg" alt="" aria-hidden="true">
      <strong>Exposed</strong>
      <span class="boundary-subtitle">typed SQL toolkit</span>
    </div>
    <div class="boundary-arrow boundary-arrow-bottom" aria-hidden="true"></div>
    <ul class="boundary-capabilities">
      <li>Makes it explicit</li>
      <li>Typed SQL expressions</li>
      <li>JDBC or R2DBC</li>
    </ul>
  </section>

  <section class="boundary-world boundary-database">
    <div class="boundary-world-heading">
      <img class="boundary-database-logo" src="/Postgresql_elephant.svg" alt="" aria-hidden="true">
      <span>Database</span>
    </div>
    <ul class="boundary-concepts">
      <li>Tables and rows</li>
      <li>Keys and constraints</li>
      <li>SQL</li>
    </ul>
  </section>
</div>


---

# What is Exposed?

**JetBrains’ Kotlin SQL library**

- **Type-safe SQL DSL** — `exposed-core`
- **Optional DAO API** — `exposed-dao`
- **JDBC or R2DBC** — `exposed-jdbc` or `exposed-r2dbc`
- **Spring starters** — `exposed-spring-boot-starter`, `spring-transaction`
- **Integrations** -- `exposed-java-time`, `exposed-json`, `exposed-money`, ...

---

# One table foundation, two access styles

> `Table` is the shared foundation. DAO entities map records from `IdTable`s.

<ExposedArchitectureGraph />

---

# Defining our Tables

<DrawnAnnotation text="Table(&quot;Profile&quot;)" label="Default tableName = className - Table suffix => Profile" on="0">
<DrawnAnnotation text="Table(&quot;profiles&quot;)" label="Follow database conventions" on="1">
<DrawnAnnotation text="Column<Long>" label="Every column is typed to a proper Kotlin type" on="2">
<DrawnAnnotation text="PrimaryKey" label="Explicitly create PrimaryKey" on="2">
<DrawnAnnotation text="val name" label="Create properties to match the shape of table" on="3">

<DrawnAnnotation text=".autoIncrement().entityId()" label="Auto-incrementing database identifier as EntityID" on="4">

<DrawnAnnotation text="IdTable<Long>" on="5">
<DrawnAnnotation text="override" on="5">
<DrawnAnnotation text="override" occurrence="2" label="Auto-incrementing database identifier as EntityID" on="5">

<DrawnAnnotation text="LongIdTable" label="LongIdTable implements all this boilerplate for us" on="6">

<DrawnAnnotation text="reference(&quot;speaker_id&quot;, ProfileTable.id)" label="Referencing a table requires the EntityID for the table" on="7">

<DrawnAnnotation text="ProfileTable" occurrence="2" label="(Long)IdTable has specialised syntax for referencing, joins, DAO, and more" on="8">

<DrawnAnnotation text="ReferenceOption" on="9">
<DrawnAnnotation text="ReferenceOption" label="CASCADE, SET_NULL, RESTRICT, NO_ACTION, SET_DEFAULT;" occurrence="2" on="9">

<DrawnAnnotation text="columnName = " on="10">
<DrawnAnnotation text="sequenceName = " label="Override default column names" on="10">
<DrawnAnnotation text="fkName = " on="10">


````md magic-move
```kotlin
object ProfileTable : Table("Profile")
```

```kotlin
object ProfileTable : Table("profiles")
```

```kotlin
object ProfileTable : Table("profiles") {
    val id: Column<Long> = long("id")
    val primaryKey = PrimaryKey(id)
}
```

```kotlin
object ProfileTable : Table("profiles") {
    val id = long("id")
    val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```

```kotlin
object ProfileTable : Table("profiles") {
    val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
    val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```

```kotlin
object ProfileTable : IdTable<Long>("profiles") {
    override val id = long("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```

```kotlin
object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
}
```

```kotlin
object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
}

object TalksTable : LongIdTable("talks") {
    val speakerId = reference("speaker_id", ProfileTable.id)
    val title = varchar("title", 200)
    val description = text("description")
}
```

```kotlin
object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
}

object TalksTable : LongIdTable("talks") {
    val speakerId = reference("speaker_id", ProfileTable)
    val title = varchar("title", 200)
    val description = text("description")
}
```

```kotlin
object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
}

object TalksTable : LongIdTable("talks") {
    val speakerId = reference(
        "speaker_id",
        ProfileTable,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
    )
    val title = varchar("title", 200)
    val description = text("description")
}
```
```kotlin
object ProfileTable : LongIdTable(
    "profiles",
    columnName = "id",
    sequenceName = "profiles_id_seq"
) {
    val name = varchar("name", 120)
}

object TalksTable : LongIdTable("talks") {
    val speakerId = reference(
        "speaker_id",
        ProfileTable,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_talks_speaker_id"
    )
    val title = varchar("title", 200)
    val description = text("description")
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
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>

---
layout: two-cols-header
---

# `exposed-core` Numeric types

::left::

| `byte()`    | `Byte`       |
|-------------|--------------|
| `short()`   | `Short`      |
| `integer()` | `Int`        |
| `long()`    | `Long`       |
| `float()`   | `Float`      |
| `double()`  | `Double`     |
| `decimal()` | `BigDecimal` |

::right::

| `ubyte()`    | `UByte`  |
|--------------|----------|
| `ushort()`   | `UShort` |
| `uinteger()` | `UInt`   |
| `ulong()`    | `ULong`  |

---

# `exposed-core` Kotlin types

| `char()` / `char(length)`     | `Char`             |
|-------------------------------|--------------------|
| `varchar()`                   | `String`           |
| `text()`                      | `String`           |
| `binary()` / `binary(length)` | `ByteArray`        |
| `bool()`                      | `Boolean`          |
| `uuid()`                      | `kotlin.uuid.Uuid` |

---

# Columns can expose domain types

<DrawnAnnotation text="varchar(&quot;title&quot;, 200)" label="Database stores varchar" on="0">
<DrawnAnnotation text="Column<TalkTitle>" label="Our application sees TalkTitle" on="0">

```kotlin
@JvmInline
value class TalkTitle(val value: String)

object Talks : Table("talks") {
    val title: Column<TalkTitle> = varchar("title", 200)
        .transform(::TalkTitle) { it.value }
}
```

</DrawnAnnotation>
</DrawnAnnotation>

---

# Enums and containers

<br>

<DrawnAnnotation text="enumeration<E>()" label="Database stores ordinal which is dangerous for schema evolution" on="0" color="red" placement="up">

| `enumeration<E>()`       | `E : Enum<E>`              |
|--------------------------|----------------------------|
| `enumerationByName<E>()` | `E : Enum<E>`              |
| `array<E>(Column<E>)`    | `List<E>` / nested `List`  |
| `vector()`               | `FloatArray` / `IntArray`  |
| `javaUUID()`             | `java.util.UUID`           |
| `blob()`                 | `ExposedBlob(InputStream)` |

</DrawnAnnotation>

---

# `exposed-kotlin-datetime`

| `date()`                  | `kotlinx.datetime.LocalDate`     |
|---------------------------|----------------------------------|
| `time()`                  | `kotlinx.datetime.LocalTime`     |
| `datetime()`              | `kotlinx.datetime.LocalDateTime` |
| `timestamp()`             | `kotlin.time.Instant`            |
| `timestampWithTimeZone()` | `java.time.OffsetDateTime`       |
| `duration()`              | `kotlin.time.Duration`           |

---

# `exposed-java-time`

| `date()`                  | `java.time.LocalDate`      |
|---------------------------|----------------------------|
| `time()`                  | `java.time.LocalTime`      |
| `datetime()`              | `java.time.LocalDateTime`  |
| `timestamp()`             | `java.time.Instant`        |
| `timestampWithTimeZone()` | `java.time.OffsetDateTime` |
| `duration()`              | `java.time.Duration`       |

---

# `exposed-jodatime` (legacy)

| `date()`                  | `org.joda.time.DateTime`  |
|---------------------------|---------------------------|
| `time()`                  | `org.joda.time.LocalTime` |
| `datetime()`              | `org.joda.time.DateTime`  |
| `timestampWithTimeZone()` | `org.joda.time.DateTime`  |

---

# `exposed-json`

| `json<T>()` / `json<T>(serialize, deserialize)`   | `T` |
|---------------------------------------------------|-----|
| `jsonb<T>()` / `jsonb<T>(serialize, deserialize)` | `T` |

<div class="code-swap">
<div v-click.hide="1">
<DrawnAnnotation text="@Serializable" label="Works based on kotlinx.serialization" :on="0">

```kotlin
@Serializable
data class ProfilesAttributes(
    val name: String,
    @SerialName("relationship_status")
    val relationshipStatus: String,
)
```

</DrawnAnnotation>
</div>

<div v-click="1">
<DrawnAnnotation text="jacksonObjectMapper" label="Custom Jackson implementation" :on="1">
<DrawnAnnotation text="mapper.writeValueAsString" :on="1">
<DrawnAnnotation text="mapper.readValue<MyDataClass>" :on="1">

```kotlin{1,5-9}
val mapper = jacksonObjectMapper()

object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
    val attributes = json(
        "attributes",
        serialize = { obj -> mapper.writeValueAsString(obj) },
        deserialize = { json -> mapper.readValue<MyDataClass>(json) }
    )
}
```

</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>
</div>
</div>

<style>
.code-swap {
  display: grid;
}

.code-swap > .slidev-vclick-target {
  grid-area: 1 / 1;
  transition: opacity 300ms ease;
}
</style>

---

# `exposed-crypt`

> Both builders require an `Encryptor`
>
> Exposed transforms values before database I/O.

| `encryptedVarchar()` | `String`    |
|----------------------|-------------|
| `encryptedBinary()`  | `ByteArray` |

---

# `exposed-money`

> DAO, transport, migration, and Spring modules add no column value mappings.

<DrawnAnnotation text="compositeMoney" on="0">
<DrawnAnnotation text="compositeMoney" occurrence="2" label="MonetaryAmount value backed by 2 columns BigDecimal & CurrencyUnit" on="0">

| `currency()`                                                                                 | `javax.money.CurrencyUnit`   |
|----------------------------------------------------------------------------------------------|------------------------------|
| `compositeMoney(precision, scale)`                                                           | `javax.money.MonetaryAmount` |
| <code>compositeMoney(<br>&nbsp;&nbsp;amountColumn,<br>&nbsp;&nbsp;currencyColumn<br>)</code> | `javax.money.MonetaryAmount` |

</DrawnAnnotation>
</DrawnAnnotation>
