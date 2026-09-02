---
layout: intro
class: section-slide
kodee: wave
---

<div class="lesson-number">Topic 3</div>

# Advanced queries

## Compose the query your product actually needs

---

# A relational query is built in layers

> Upcoming talks with speaker, host, tags, and bookmark count.

1. **Alias** — give repeated roles distinct names.
2. **Aggregate** — derive a value from many rows.
3. **Filter** — compose an API query safely.

---

# Keep talks with zero bookmarks

<DrawnAnnotation text="count().alias(&quot;bookmark_count&quot;)" />
<DrawnAnnotation text="bookmarkCount" occurrence="2" label="Use the aliased column in select"  :geometry="{ label: { x: 0.6943, y: 0.4173 }, connector: { start: { x: 0.4324, y: 0.3761 }, end: { x: 0.5345, y: 0.4092 } } }"/>
<DrawnAnnotation text="COUNT(bookmarks.talk_id) bookmark_count" />

```kotlin
val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")

Talks.leftJoin(Bookmarks)
  .select(Talks.title, bookmarkCount)
  .groupBy(Talks.id, Talks.title)
  .orderBy(bookmarkCount, SortOrder.DESC)
```

```sql
SELECT talks.title, COUNT(bookmarks.talk_id) bookmark_count
FROM talks
       LEFT JOIN bookmarks ON talks.id = bookmarks.talk_id
GROUP BY talks.id, talks.title
ORDER BY bookmark_count DESC
```

---

# Keep talks with zero bookmarks

<DrawnAnnotation text="this[bookmarkCount]" label="Use the aliased column in `ResultRow` extraction"  :geometry="{ label: { x: 0.7379, y: 0.3449, width: 0.3015 }, connector: { start: { x: 0.6870, y: 0.5539 }, end: { x: 0.7153, y: 0.3940 } } }"/>

```kotlin
val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")

Talks.leftJoin(Bookmarks)
  .select(Talks.title, bookmarkCount)
  .groupBy(Talks.id, Talks.title)
  .orderBy(bookmarkCount, SortOrder.DESC)

fun ResultRow.bookmarkCount(): Int = this[bookmarkCount]
```

```sql
SELECT talks.title, COUNT(bookmarks.talk_id) bookmark_count
FROM talks
       LEFT JOIN bookmarks ON talks.id = bookmarks.talk_id
GROUP BY talks.id, talks.title
ORDER BY bookmark_count DESC
```

---
magic-move
---

# Keep only talks with 10+ bookmarks

<DrawnAnnotation text="having" :connect="false" label="`having` after `groupBy` to filter the aggregate" on="0" :geometry="{ label: { x: 0.6802, y: 0.3743, width: 0.7196 } }"/>
<DrawnAnnotation text="Bookmarks.talkId.count()" label="PostgreSQL doesn't make SELECT aliases available but doesn't run it twice" occurrence="2" :connect="false" on="1" :geometry="{ label: { x: 0.7591, y: 0.3499, width: 0.3836 } }"/>

```kotlin
val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")

Talks.leftJoin(Bookmarks)
  .select(Talks.title, bookmarkCount)
  .groupBy(Talks.id, Talks.title)
  .having { Bookmarks.talkId.count() greaterEq 10 }
  .orderBy(bookmarkCount, SortOrder.DESC)
```

```sql
SELECT talks.title, COUNT(bookmarks.talk_id) bookmark_count
FROM talks
       LEFT JOIN bookmarks ON talks.id = bookmarks.talk_id
GROUP BY talks.id, talks.title
HAVING COUNT(bookmarks.talk_id) >= 10
ORDER BY bookmark_count DESC
```

---

# Aggregates answer questions across rows

> `where` filters → `groupBy` groups → `having` filters groups

| Expression       | Question            | Example                  |
|------------------|---------------------|--------------------------|
| `count()`        | How many?           | Bookmarks per talk       |
| `sum()`          | What total?         | Ticket revenue           |
| `average()`      | What typical value? | Session rating           |
| `having { ... }` | Which groups?       | Talks with 10+ bookmarks |

---

# A query can become a value in another query

```kotlin
fun talkIdsForTag(tag: String): Query =
  TalkTags.innerJoin(Tags)
    .select(TalkTags.talkId)
    .where { Tags.label eq tag }
```
```sql
SELECT talk_tags.talk_id
FROM talk_tags
INNER JOIN tags ON tags.id = talk_tags.tag_id
WHERE tags."label" = 'kotlin'
```

---
magic-move
---

# A query can become a value in another query

<DrawnAnnotation text="where { Talks.id inSubQuery" label="Check if a queries result row contains the element `Talks.id`"  :geometry="{ label: { x: 0.6190, y: 0.6306, width: 0.2099 } }"/>
<DrawnAnnotation text="WHERE talks.id IN" />

```kotlin
fun talkIdsForTag(tag: String): Query =
  TalkTags.innerJoin(Tags)
    .select(TalkTags.talkId)
    .where { Tags.label eq tag }

Talks.select(Talks.title)
  .where { Talks.id inSubQuery talkIdsForTag("kotlin") }
```
```sql
SELECT talks.title
FROM talks
WHERE talks.id IN (
  SELECT talk_tags.talk_id
  FROM talk_tags
  INNER JOIN tags ON tags.id = talk_tags.tag_id
  WHERE tags."label" = 'kotlin'
)
```

---

# A query can become a predicate in another query

<DrawnAnnotation text="exists" label="If the `hasTag` query returns at least one row"  :geometry="{ label: { x: 0.5943, y: 0.6619, width: 0.5428 }, connector: { start: { x: 0.2526, y: 0.5294 }, end: { x: 0.4005, y: 0.6292 } } }"/>

```kotlin
fun hasTag(tag: String) =
  TalkTags.innerJoin(Tags)
    .select(TalkTags.talkId)
    .where { TalkTags.talkId eq Talks.id and (Tags.label eq tag) }

Talks.select(Talks.title)
  .where { exists(hasTag("kotlin")) }
```
```sql
SELECT talks.title
FROM talks
WHERE EXISTS (
  SELECT talk_tags.talk_id
  FROM talk_tags 
    INNER JOIN tags ON tags.id = talk_tags.tag_id
  WHERE talk_tags.talk_id = talks.id AND tags."label" = 'kotlin'
)
```

---

# Expressions can call SQL functions

<DrawnAnnotation text="ProfileTable.name.trim().lowerCase()" label="Create complex expressions by composing functions" on="0" />

```kotlin
val normalizedName = ProfileTable.name.trim().lowerCase()
  .alias("normalized_name")
```

---

# Expressions can call SQL functions

<DrawnAnnotation text="Expression<T>.unaccent()" on="0"  :geometry="{ label: { x: 0.6858, y: 0.2590 }, connector: { start: { x: 0.5382, y: 0.5926 }, end: { x: 0.6131, y: 0.5261 } } }"/>
<DrawnAnnotation text=".unaccent()" occurrence="2" on="0" label="Create custom SQL functions with elegant syntax"/>
<DrawnAnnotation text="&quot;unaccent&quot;" label="`exec(&quot;CREATE EXTENSION IF NOT EXISTS unaccent;&quot;)`" on="1"  :geometry="{ label: { x: 0.7005, y: 0.4401, width: 0.6166 }, connector: { start: { x: 0.4267, y: 0.4145 }, end: { x: 0.4443, y: 0.4249 } } }"/>


```kotlin
val normalizedName = ProfileTable.name.trim().lowerCase()
  .alias("normalized_name")

fun <T : String?>  Expression<T>.unaccent() =
  CustomStringFunction("unaccent", this)

val normalizedTitle = Talks.title.lowerCase().unaccent()
  .alias("normalized_title")
```

---

# Expressions can call SQL functions

<DrawnAnnotation text="normalizedName, normalizedTitle" label="Reference them in select"  :geometry="{ label: { x: 0.7094, y: 0.4752 } }"/>
<DrawnAnnotation text="LOWER(TRIM(profiles.&quot;name&quot;)) normalized_name" />
<DrawnAnnotation text="unaccent(LOWER(talks.title)) normalized_title" />


```kotlin
val normalizedName = ProfileTable.name.trim().lowerCase().alias("normalized_name")

val normalizedTitle = Talks.title.lowerCase().unaccent().alias("normalized_title")

Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  .select(normalizedName, normalizedTitle)
  .orderBy(normalizedTitle to SortOrder.ASC)
```
```sql
SELECT(
       LOWER(TRIM(profiles."name")) normalized_name,
       unaccent(LOWER(talks.title)) normalized_title  
) 
FROM talks INNER JOIN profiles ON (talks.speaker_id = profiles.id)
ORDER BY normalized_title ASC
```

---

# Expressions can call SQL functions

<DrawnAnnotation text="normalizedName" occurrence="3" /> 
<DrawnAnnotation text="normalizedTitle" occurrence="4" label="Reference them in the ResultRow"  :geometry="{ label: { x: 0.7251, y: 0.3820 } }"/> 

```kotlin
Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  .select(normalizedName, normalizedTitle)
  .orderBy(normalizedTitle to SortOrder.ASC)
  .map { row ->
    NormalizedTalk(
      normalizedName = row[normalizedName],
      normalizedTitle = row[normalizedTitle],
    )
  }
```
```sql
SELECT(
       LOWER(TRIM(profiles."name")) normalized_name,
       unaccent(LOWER(talks.title)) normalized_title  
) 
FROM talks INNER JOIN profiles ON (talks.speaker_id = profiles.id)
ORDER BY normalized_title ASC
```

---

# For special SQL syntax, implement an expression

<DrawnAnnotation text="ExpressionWithColumnType<Long>" label="Typed definition gives us type safety" on="0"  :geometry="{ label: { x: 0.8259, y: 0.2830, width: 0.1924 } }"/>
<DrawnAnnotation text="QueryBuilder" occurrence="2" label="Full control over the SQL" on="1"  :geometry="{ label: { x: 0.6615, y: 0.4555 } }"/>

```kotlin
private object TotalCount : ExpressionWithColumnType<Long>() {
  override val columnType = LongColumnType()

  override fun toQueryBuilder(queryBuilder: QueryBuilder) {
    queryBuilder.append("COUNT(*) OVER ()")
  }
}
```

---

<DrawnAnnotation text="TotalCount" />
<DrawnAnnotation text="row[TotalCount]" label="Returns `Long` through `ExpressionWithColumnType<Long>`"  :geometry="{ label: { x: 0.6709, y: 0.4581, width: 0.4627 }, connector: { start: { x: 0.4383, y: 0.4221 }, end: { x: 0.5534, y: 0.4451 } } }"/>

```kotlin
Talks.select(Talks.id, Talks.title, TotalCount)
  .where { Talks.isPublished eq true }
  .map { row ->
    TalkPageRow(
      id = row[Talks.id].value,
      title = row[Talks.title],
      totalCount = row[TotalCount],
    )
  }
```
```sql
SELECT talks.id, talks.title, COUNT(*) OVER ()
FROM talks
WHERE talks.is_published = TRUE

```

---

# Reach for relational tools deliberately

- `innerJoin` / `leftJoin` — related rows and unmatched left rows
- `alias` — distinct roles, computed expressions, derived queries
- `groupBy` / `having` — aggregates at a clear output grain
- `subquery` / `Op<Boolean>` — typed API filters
- `Function<T>` — carefully extend SQL when built-ins are insufficient
