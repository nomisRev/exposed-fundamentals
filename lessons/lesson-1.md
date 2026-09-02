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
    <br/>
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
    <br/>
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
- **Spring starters** — `exposed-spring-boot4-starter`, `spring-transaction`
- **Integrations** -- `exposed-java-time`, `exposed-json`, `exposed-money`, ...

---
layout: full
class: database-landscape-slide
kodee: false
---

<div class="database-landscape" role="img" aria-label="What is Exposed? Exposed connects Kotlin applications to PostgreSQL, Microsoft SQL Server, MySQL, Amazon Redshift, MariaDB, Oracle, SQLite, and H2">
  <h1 class="database-landscape-title">What is Exposed?</h1>
  <img class="database-landscape-exposed" src="/Exposed icon.svg" alt="" aria-hidden="true">
  <img class="database-logo database-logo-postgresql" src="/postgresql.svg" alt="PostgreSQL">
  <img class="database-logo database-logo-microsoft-sql-server" src="/microsoft-sql-server.svg" alt="Microsoft SQL Server">
  <img class="database-logo database-logo-mysql" src="/mysql.svg" alt="MySQL">
  <img class="database-logo database-logo-redshift" src="/amazon-redshift.svg" alt="Amazon Redshift">
  <img class="database-logo database-logo-mariadb" src="/mariadb.svg" alt="MariaDB">
  <img class="database-logo database-logo-oracle" src="/oracle.svg" alt="Oracle">
  <img class="database-logo database-logo-sqlite" src="/sqlite.svg" alt="SQLite">
  <img class="database-logo database-logo-h2" src="/h2.svg" alt="H2">
</div>

---

# One table foundation, two access styles

> `Table` is the shared foundation. DAO entities map records from `IdTable`s.

<ExposedArchitectureGraph />

---

# Defining our Tables

<DrawnAnnotation text="Table(&quot;Profile&quot;)" label="Default `tableName =` className - `Table` suffix => Profile" on="0" :geometry="{ label: { x: 0.3972, y: 0.3123 }, connector: { start: { x: 0.3566, y: 0.2303 }, end: { x: 0.3576, y: 0.2737 } } }" />

```kotlin
object ProfileTable : Table("Profile")
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="Table(&quot;profiles&quot;)" label="PostgreSQL conventions are plural lower snake_case. Automatically folds all unquoted identifiers to lowercase"  :geometry="{ label: { x: 0.5228, y: 0.3274 } }" />

```kotlin
object ProfileTable : Table("profiles")
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="Column<Long>" label="Every column is typed to a proper Kotlin type"  :geometry="{ label: { x: 0.6369, y: 0.2841, width: 0.3862 }, connector: { start: { x: 0.3056, y: 0.2676 }, end: { x: 0.5251, y: 0.2767 } } }" />
<DrawnAnnotation text="PrimaryKey" label="Explicitly create PrimaryKey"  :geometry="{ label: { x: 0.3151, y: 0.4306 }, connector: { start: { x: 0.3157, y: 0.3282 }, end: { x: 0.3157, y: 0.4041 } } }" />

```kotlin
object ProfileTable : Table("profiles") {
  val id: Column<Long> = long("id")
  val primaryKey = PrimaryKey(id)
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="val name" />
<DrawnAnnotation text="val bio" label="Create properties to match the shape of table"  />

```kotlin
object ProfileTable : Table("profiles") {
  val id = long("id")
  val primaryKey = PrimaryKey(id)
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text=".autoIncrement().entityId()" label="Auto-incrementing database identifier as `EntityID`"  :geometry="{ label: { x: 0.6843, y: 0.3497 }, connector: { start: { x: 0.6696, y: 0.2813 }, end: { x: 0.6862, y: 0.3268 } } }" />

```kotlin
object ProfileTable : Table("profiles") {
  val id: Column<EntityID<Long>> = long("id").autoIncrement().entityId()
  val primaryKey = PrimaryKey(id)
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="IdTable<Long>" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="override" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="override" occurrence=2 label="Enforces auto-incrementing `EntityID` as 'contract' with `IdTable`"  color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.4853, y: 0.5033, width: 0.6791 }, connector: { type: 'quadratic', start: { x: 0.1667, y: 0.3249 }, control: { x: 0.1688, y: 0.4006 }, end: { x: 0.1900, y: 0.4527 } } }" />

```kotlin
object ProfileTable : IdTable<Long>("profiles") {
  override val id = long("id").autoIncrement().entityId()
  override val primaryKey = PrimaryKey(id)
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="LongIdTable" label="`LongIdTable` implements all this boilerplate for us" :geometry="{ label: { x: 0.6854, y: 0.2628, width: 0.4913 }, connector: { start: { x: 0.4272, y: 0.2253 }, end: { x: 0.4956, y: 0.2401 } } }" />

```kotlin
object ProfileTable : LongIdTable("profiles") {
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="reference(&quot;speaker_id&quot;, ProfileTable.id)" label="Referencing a 'foreign' table requires the `EntityID` for the referenced `Table`"  :geometry="{ label: { x: 0.6192, y: 0.7158 }, connector: { start: { x: 0.5496, y: 0.5208 }, end: { x: 0.5902, y: 0.6747 } } }" />

```kotlin
object ProfileTable : LongIdTable("profiles") {
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}

object TalksTable : LongIdTable("talks") {
  val speakerId = reference("speaker_id", ProfileTable.id)
  val title = varchar("title", 200)
  val description = text("description")
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="ProfileTable" occurrence=2 label="`(Long)IdTable` has specialised syntax for referencing, joins, DAO, and more"  :geometry="{ label: { x: 0.5613, y: 0.6980 } }" />

```kotlin
object ProfileTable : LongIdTable("profiles") {
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
}

object TalksTable : LongIdTable("talks") {
  val speakerId = reference("speaker_id", ProfileTable)
  val title = varchar("title", 200)
  val description = text("description")
}
```

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="ReferenceOption" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="ReferenceOption" label="`CASCADE, SET_NULL, RESTRICT, NO_ACTION, SET_DEFAULT;`" occurrence=2  color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.6232, y: 0.7100, width: 0.1234 }, connector: { type: 'quadratic', start: { x: 0.3931, y: 0.6974 }, control: { x: 0.4738, y: 0.7468 }, end: { x: 0.5551, y: 0.7407 } } }" />

```kotlin
object ProfileTable : LongIdTable("profiles") {
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
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

---
magic-move
---

# Defining our Tables

<DrawnAnnotation text="columnName = " color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="sequenceName = " label="Override default column names" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.6311, y: 0.4739 }, connector: { type: 'quadratic', start: { x: 0.2552, y: 0.3719 }, control: { x: 0.4225, y: 0.3825 }, end: { x: 0.5232, y: 0.4358 } } }" />
<DrawnAnnotation text="fkName = " color="var(--drawn-annotation-color)" :sequential="false" />

```kotlin
object ProfileTable : LongIdTable(
  "profiles",
  columnName = "id",
  sequenceName = "profiles_id_seq"
) {
  val name = varchar("name", 120)
  val bio = text("bio").nullable()
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

| `char()`                      | `Char`             |
|-------------------------------|--------------------|
| `char(length)`                | `String`           |
| `varchar()`                   | `String`           |
| `text()`                      | `String`           |
| `binary()` / `binary(length)` | `ByteArray`        |
| `bool()`                      | `Boolean`          |
| `uuid()`                      | `kotlin.uuid.Uuid` |

---

# Columns can expose domain types

<DrawnAnnotation text="Column<TalkTitle>" label="Kotlin sees `TalkTitle`" on="0" :geometry="{ label: { x: 0.2937, y: 0.5805 }, connector: { type: 'quadratic', start: { x: 0.3190, y: 0.5356 }, control: { x: 0.3564, y: 0.4959 }, end: { x: 0.3588, y: 0.4276 } } }" />
<DrawnAnnotation text="varchar(&quot;title&quot;, 200)" label="Database stores `varchar`" on="0" :geometry="{ label: { x: 0.7655, y: 0.4677 }, connector: { type: 'quadratic', start: { x: 0.5568, y: 0.4260 }, control: { x: 0.5736, y: 0.4764 }, end: { x: 0.6392, y: 0.4694 } } }" />
<DrawnAnnotation text="Column<TalkTitle>" label="Can complicate defining complex `String` expressions" on="1"  :geometry="{ label: { x: 0.5906, y: 0.5520 } }"/>

```kotlin
@JvmInline
value class TalkTitle(val value: String)

object Talks : Table("talks") {
  val title: Column<TalkTitle> = varchar("title", 200)
    .transform(::TalkTitle) { it.value }
}
```

---

# Enums and containers

<br>

<DrawnAnnotation text="enumeration<E>()" label="Database stores ordinal which is dangerous for schema evolution" color="red" :geometry="{ label: { x: 0.6855, y: 0.1305, width: 0.5673 }, connector: { type: 'quadratic', start: { x: 0.2676, y: 0.3007 }, control: { x: 0.4075, y: 0.2777 }, end: { x: 0.5460, y: 0.1872 } } }" />

| `enumeration<E>()`       | `E : Enum<E>`              |
|--------------------------|----------------------------|
| `enumerationByName<E>()` | `E : Enum<E>`              |
| `array<E>(Column<E>)`    | `List<E>` / nested `List`  |
| `vector()`               | `FloatArray` / `IntArray`  |
| `javaUUID()`             | `java.util.UUID`           |
| `blob()`                 | `ExposedBlob(InputStream)` |

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

<DrawnAnnotation text="json<T>()" on="0" />
<DrawnAnnotation text="jsonb<T>()" on="0" />

<DrawnAnnotation text="json<T>(serialize, deserialize)" on="1" />
<DrawnAnnotation text="jsonb<T>(serialize, deserialize)" on="1" />

| `json<T>()` / `json<T>(serialize, deserialize)`   | `T` |
|---------------------------------------------------|-----|
| `jsonb<T>()` / `jsonb<T>(serialize, deserialize)` | `T` |

<div class="code-swap">
<div v-click.hide="1">

<DrawnAnnotation text="@Serializable" label="Works based on kotlinx.serialization" :geometry="{ label: { x: 0.6945, y: 0.4483 }, connector: { type: 'quadratic', start: { x: 0.1983, y: 0.4161 }, control: { x: 0.3895, y: 0.4026 }, end: { x: 0.5163, y: 0.4484 } } }" />

```kotlin
@Serializable
data class ProfilesAttributes(
  val name: String,
  @SerialName("relationship_status")
  val relationshipStatus: String,
)
```

</div>

<div v-click="1">

<DrawnAnnotation text="jacksonObjectMapper" label="Custom Jackson implementation" :on="1" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.7494, y: 0.5173 }, connector: { type: 'quadratic', start: { x: 0.4189, y: 0.4240 }, control: { x: 0.5095, y: 0.4357 }, end: { x: 0.5969, y: 0.4884 } } }" />
<DrawnAnnotation text="mapper.writeValueAsString" :on="1" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="mapper.readValue<MyDataClass>" :on="1" color="var(--drawn-annotation-color)" :sequential="false" />

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

<DrawnAnnotation text="compositeMoney" color="var(--drawn-annotation-color)" :sequential="false" />
<DrawnAnnotation text="compositeMoney" occurrence=2 label="`MonetaryAmount` value backed by 2 columns `BigDecimal` & `CurrencyUnit`" color="var(--drawn-annotation-color)" :sequential="false" :geometry="{ label: { x: 0.4943, y: 0.8808, width: 0.4574 }, connector: { type: 'quadratic', start: { x: 0.2526, y: 0.5876 }, control: { x: 0.4187, y: 0.7023 }, end: { x: 0.4521, y: 0.8144 } } }" />

| `currency()`                                                                                 | `javax.money.CurrencyUnit`   |
|----------------------------------------------------------------------------------------------|------------------------------|
| `compositeMoney(precision, scale)`                                                           | `javax.money.MonetaryAmount` |
| <code>compositeMoney(<br>&nbsp;&nbsp;amountColumn,<br>&nbsp;&nbsp;currencyColumn<br>)</code> | `javax.money.MonetaryAmount` |
