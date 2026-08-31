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

<DrawnAnnotation text="Table(&quot;Profile&quot;)" label="Default tableName = className - Table suffix => Profile"  :geometry="{ connector: { type: 'polyline', points: [{ x: 0.3566, y: 0.2221 }, { x: 0.3566, y: 0.2495 }] } }">

```kotlin
object ProfileTable : Table("Profile")
```
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="Table(&quot;profiles&quot;)" label="Follow database conventions"  :geometry="{ label: { x: 0.5228, y: 0.3274 } }">

```kotlin
object ProfileTable : Table("profiles")
```
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="Column<Long>" label="Every column is typed to a proper Kotlin type"  :geometry="{ label: { x: 0.6345, y: 0.2683, width: 0.3862 }, connector: { type: 'polyline', points: [{ x: 0.3013, y: 0.2574 }, { x: 0.5208, y: 0.2665 }] } }">
<DrawnAnnotation text="PrimaryKey" label="Explicitly create PrimaryKey"  :geometry="{ label: { x: 0.3143, y: 0.4041 }, connector: { type: 'polyline', points: [{ x: 0.3156, y: 0.3059 }, { x: 0.3156, y: 0.3818 }] } }">

```kotlin
object ProfileTable : Table("profiles") {
    val id: Column<Long> = long("id")
    val primaryKey = PrimaryKey(id)
}
```
</DrawnAnnotation>
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="val name" label="Create properties to match the shape of table"  :geometry="{ label: { x: 0.3592, y: 0.4755 }, connector: { type: 'polyline', points: [{ x: 0.1509, y: 0.3572 }, { x: 0.1690, y: 0.4447 }] } }">

```kotlin
object ProfileTable : Table("profiles") {
    val id = long("id")
    val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text=".autoIncrement().entityId()" label="Auto-incrementing database identifier as EntityID"  :geometry="{ connector: { type: 'polyline', points: [{ x: 0.6677, y: 0.2664 }, { x: 0.6677, y: 0.2937 }] } }">

```kotlin
object ProfileTable : Table("profiles") {
    val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
    val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="IdTable<Long>" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="override" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="override" occurrence=2 label="Auto-incrementing database identifier as EntityID"  color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.3767, y: 0.4595, width: 0.4632 }, connector: { type: 'quadratic', start: { x: 0.1757, y: 0.3040 }, control: { x: 0.1778, y: 0.3797 }, end: { x: 0.1990, y: 0.4318 } } }">

```kotlin
object ProfileTable : IdTable<Long>("profiles") {
    override val id = long("id").autoIncrement().entityId()
    override val primaryKey = PrimaryKey(id)
    val name = varchar("name", 120)
}
```
</DrawnAnnotation>
</DrawnAnnotation>
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="LongIdTable" label="`LongIdTable` implements all this boilerplate for us" :geometry="{ label: { x: 0.6854, y: 0.2628, width: 0.4913 }, connector: { type: 'polyline', points: [{ x: 0.3950, y: 0.2175 }, { x: 0.4634, y: 0.2323 }] } }">

```kotlin
object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
}
```
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="reference(&quot;speaker_id&quot;, ProfileTable.id)" label="Referencing a table requires the `EntityID` for the table"  :geometry="{ label: { x: 0.6185, y: 0.6367 }, connector: { type: 'polyline', points: [{ x: 0.5525, y: 0.4407 }, { x: 0.5931, y: 0.5946 }] } }">

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
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="ProfileTable" occurrence=2 label="`(Long)IdTable` has specialised syntax for referencing, joins, DAO, and more"  :geometry="{ label: { x: 0.5608, y: 0.6211 } }">

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
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="ReferenceOption" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="ReferenceOption" label="`CASCADE, SET_NULL, RESTRICT, NO_ACTION, SET_DEFAULT;`" occurrence=2  color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.6680, y: 0.6256, width: 0.4285 }, connector: { type: 'polyline', points: [{ x: 0.4084, y: 0.6099 }, { x: 0.5175, y: 0.6314 }] } }">

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
</DrawnAnnotation>
</DrawnAnnotation>


---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="columnName = " color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="sequenceName = " label="Override default column names" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.6265, y: 0.4533 }, connector: { type: 'quadratic', start: { x: 0.2321, y: 0.3581 }, control: { x: 0.3994, y: 0.3687 }, end: { x: 0.5001, y: 0.4220 } } }">
<DrawnAnnotation text="fkName = " color="var(--drawn-annotation-color)" :sequential="false">

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

<DrawnAnnotation text="Column<TalkTitle>" label="Our application sees `TalkTitle`"  :geometry="{ label: { x: 0.3176, y: 0.5295 }, connector: { type: 'quadratic', start: { x: 0.3338, y: 0.5081 }, control: { x: 0.3712, y: 0.4684 }, end: { x: 0.3736, y: 0.4001 } } }">
<DrawnAnnotation text="varchar(&quot;title&quot;, 200)" label="Database stores `varchar`"  :geometry="{ label: { x: 0.7435, y: 0.4465 }, connector: { type: 'quadratic', start: { x: 0.5398, y: 0.4026 }, control: { x: 0.5566, y: 0.4530 }, end: { x: 0.6222, y: 0.4460 } } }">

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

<DrawnAnnotation text="`enumeration<E>()`" label="Database stores ordinal which is dangerous for schema evolution" color="red" placement="up" :geometry="{ label: { x: 0.6855, y: 0.1305, width: 0.5673 }, connector: { type: 'quadratic', start: { x: 0.2676, y: 0.3007 }, control: { x: 0.4075, y: 0.2777 }, end: { x: 0.5460, y: 0.1872 } } }">

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

<DrawnAnnotation text="@Serializable" label="Works based on kotlinx.serialization" until="1" :geometry="{ label: { x: 0.6945, y: 0.4483 }, connector: { type: 'quadratic', start: { x: 0.1983, y: 0.4161 }, control: { x: 0.3895, y: 0.4026 }, end: { x: 0.5163, y: 0.4484 } } }">

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

<DrawnAnnotation text="jacksonObjectMapper" label="Custom Jackson implementation" :on="1" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.7269, y: 0.4832 }, connector: { type: 'quadratic', start: { x: 0.3873, y: 0.4190 }, control: { x: 0.4779, y: 0.4307 }, end: { x: 0.5653, y: 0.4834 } } }">
<DrawnAnnotation text="mapper.writeValueAsString" :on="1" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="mapper.readValue<MyDataClass>" :on="1" color="var(--drawn-annotation-color)" :sequential="false">

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

<DrawnAnnotation text="compositeMoney" color="var(--drawn-annotation-color)" :sequential="false">
<DrawnAnnotation text="compositeMoney" occurrence=2 label="`MonetaryAmount` value backed by 2 columns `BigDecimal` & `CurrencyUnit`" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.4943, y: 0.8808, width: 0.3562 }, connector: { type: 'quadratic', start: { x: 0.2526, y: 0.5876 }, control: { x: 0.4187, y: 0.7023 }, end: { x: 0.4521, y: 0.8144 } } }">

| `currency()`                                                                                 | `javax.money.CurrencyUnit`   |
|----------------------------------------------------------------------------------------------|------------------------------|
| `compositeMoney(precision, scale)`                                                           | `javax.money.MonetaryAmount` |
| <code>compositeMoney(<br>&nbsp;&nbsp;amountColumn,<br>&nbsp;&nbsp;currencyColumn<br>)</code> | `javax.money.MonetaryAmount` |
</DrawnAnnotation>
</DrawnAnnotation>
