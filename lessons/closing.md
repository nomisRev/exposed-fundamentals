---

# Build the final query around its output grain

1. Start with one output row per upcoming `Talk`.
2. Join the aliased `speaker` and `host` roles explicitly.
3. Aggregate `TalkTags` and `Bookmarks` in their own grouped subqueries.
4. Add an optional tag filter with `Op<Boolean>`.
5. Map `ResultRow` to the response DTO.

> **Watch multiplication:** joining both many-side tables before counting pairs every tag with every bookmark. Pre-aggregate, then join.

---

# Keep the relational model in the driver’s seat

- **Model** → keep tables, keys, and constraints visible
- **Migrate** → review DDL and assign each generated value an owner
- **Compose** → use typed SQL DSL expressions for the query shape
- **Choose** → use DAO when its entity lifecycle fits the operation

> **Exposed is Kotlin for relational work.** Keep SQL visible, make ownership explicit, and map persistence data
> deliberately at the application boundary.

## Thank you · Questions · Build the upcoming-talks query
