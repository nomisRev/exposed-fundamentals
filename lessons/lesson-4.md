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
> 
1. **Alias** — give repeated roles distinct names.
2. **Aggregate** — derive a value from many rows.
3. **Filter** — compose an API query safely.

---

# Keep talks with zero bookmarks

```kotlin
val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")

Talks.leftJoin(Bookmarks) { Talks.id eq Bookmarks.talkId }
  .select(Talks.title, bookmarkCount)
  .groupBy(Talks.id, Talks.title)
  .orderBy(bookmarkCount, SortOrder.DESC)
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

# One table, two roles: use table aliases

> Use the aliased column in both `select` and `ResultRow` extraction.

```kotlin
val speaker = ProfileTable.alias("speaker")
val host = ProfileTable.alias("host")

Talks
  .join(speaker, JoinType.INNER, Talks.speakerId, speaker[ProfileTable.id])
  .join(host, JoinType.INNER, Talks.hostId, host[ProfileTable.id])
  .select(Talks.title, speaker[ProfileTable.name], host[ProfileTable.name])
```


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
