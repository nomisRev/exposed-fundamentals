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
<DrawnAnnotation text="bookmarkCount" occurrence="2" label="Use the aliased column in select"  :geometry="{ label: { x: 0.6844, y: 0.3948 }, connector: { start: { x: 0.4248, y: 0.3486 }, end: { x: 0.5269, y: 0.3817 } } }"/>
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
FROM talks LEFT JOIN bookmarks ON talks.id = bookmarks.talk_id
GROUP BY talks.id, talks.title
ORDER BY bookmark_count DESC
```

---

# Keep talks with zero bookmarks

<DrawnAnnotation text="this[bookmarkCount]" label="Use the aliased column in `ResultRow` extraction"  :geometry="{ label: { x: 0.7379, y: 0.3449, width: 0.3015 }, connector: { start: { x: 0.6292, y: 0.5178 }, end: { x: 0.7153, y: 0.3940 } } }"/>

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
FROM talks LEFT JOIN bookmarks ON talks.id = bookmarks.talk_id
GROUP BY talks.id, talks.title
ORDER BY bookmark_count DESC
```

---

# Aggregates answer questions across rows

> `where` filters input rows → `groupBy` forms groups → `having` filters groups.

| Expression | Question | Example |
| --- | --- | --- |
| `count()` | How many? | Bookmarks per talk |
| `sum()` | What total? | Ticket revenue |
| `average()` | What typical value? | Session rating |
| `having { ... }` | Which groups? | Talks with 10+ bookmarks |

---

# A query can become a value in another query

```kotlin
fun talkIdsForTag(tag: String) = TalkTags.innerJoin(Tags)
  .select(TalkTags.talkId)
  .where { Tags.label eq tag }

Talks.select(Talks.title)
  .where { Talks.id inSubQuery talkIdsForTag("kotlin") }
```

- `inSubQuery` compares a value to query rows.
- `exists` and `notExists` ask whether a related row exists.
- Query aliases create named derived tables.

---

# Optional filters compose `Op<Boolean>`

> API input adds a predicate, not SQL text.

```kotlin
val predicates = buildList {
  tag?.let { add(Talks.id inSubQuery talkIdsForTag(it)) }
  speakerName?.let { add(ProfileTable.name eq it) }
  published?.let { add(Talks.isPublished eq it) }
}

Talks.innerJoin(ProfileTable)
  .select(Talks.columns + ProfileTable.columns)
  .where { predicates.compoundAnd() }
  .limit(size).offset(offset)
```


---

# Expressions can call SQL functions

```kotlin
val normalizedName = ProfileTable.name.trim().lowerCase().alias("normalized_name")

val normalizedTitle = CustomStringFunction(
  "unaccent", Talks.title.lowerCase(),
).alias("normalized_title")
```

`unaccent` is PostgreSQL-specific and needs its extension. Keep database-specific behaviour labelled.

---

# For special SQL syntax, implement an expression

```kotlin
private object TotalCount : ExpressionWithColumnType<Long>() {
  override val columnType = LongColumnType()

  override fun toQueryBuilder(queryBuilder: QueryBuilder) {
    queryBuilder.append("COUNT(*) OVER ()")
  }
}
```

Select `TotalCount`, then read `row[TotalCount]`. Custom emitted SQL is your dialect responsibility.

---

# Reach for relational tools deliberately

- `innerJoin` / `leftJoin` — related rows and unmatched left rows
- `alias` — distinct roles, computed expressions, derived queries
- `groupBy` / `having` — aggregates at a clear output grain
- `subquery` / `Op<Boolean>` — typed API filters
- `Function<T>` — carefully extend SQL when built-ins are insufficient

> **Exercise:** List upcoming talks with speaker, host, tag count, bookmark count, optional tag filter, and bookmark-count ordering.
