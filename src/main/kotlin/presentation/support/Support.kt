// Hand-written context for the generated snippets in ../snippets/.
//
// The slides use these tables, types, and values without defining them on
// every slide; each generated file star-imports this package. A snippet that
// defines its own `Talks` or `toTalkPreview` shadows the one here, because a
// declaration in the file's own package wins over a star import.
package presentation.support

import kotlin.time.Instant
import org.jetbrains.exposed.v1.core.CustomStringFunction
import org.jetbrains.exposed.v1.core.Expression
import org.jetbrains.exposed.v1.core.ExpressionWithColumnType
import org.jetbrains.exposed.v1.core.LongColumnType
import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.trim
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.jdbc.Database

// ---------------------------------------------------------------------------
// Tables

enum class TalkStatus { DRAFT, PUBLISHED, ARCHIVED }

object ProfileTable : LongIdTable("profiles") {
    val name = varchar("name", 120)
    val bio = text("bio").nullable()
    val avatarUrl = varchar("avatar_url", 500).nullable()
    val email = varchar("email", 320).uniqueIndex()
}

object Talks : LongIdTable("talks") {
    val speakerId = reference(
        "speaker_id",
        ProfileTable,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
    )
    val title = varchar("title", 200)
    val slug = varchar("slug", 220).uniqueIndex()
    val description = text("description")
    val startsAt = timestamp("starts_at")
    val status = enumerationByName<TalkStatus>("status", 20).default(TalkStatus.DRAFT)
    val isPublished = bool("is_published").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

object Tags : LongIdTable("tags") {
    val label = varchar("label", 80).uniqueIndex()
}

object TalkTags : Table("talk_tags") {
    val talkId = reference("talk_id", Talks, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(talkId, tagId)
}

object Bookmarks : Table("bookmarks") {
    val profileId = reference("profile_id", ProfileTable, onDelete = ReferenceOption.CASCADE)
    val talkId = reference("talk_id", Talks, onDelete = ReferenceOption.CASCADE)
    val note = varchar("note", 300).nullable()

    override val primaryKey = PrimaryKey(profileId, talkId)
}

// ---------------------------------------------------------------------------
// Domain types and row mappers

data class TalkPreview(
    val title: String,
    val description: String,
    val speakerName: String,
    val speakerAvatarUrl: String?,
)

data class TalkWithSpeakerId(
    val id: Long,
    val title: String,
    val slug: String,
    val speakerId: Long,
    val startsAt: Instant,
)

data class TalkWithoutSpeaker(
    val id: Long,
    val title: String,
    val slug: String,
    val startsAt: Instant,
)

data class TalkPageRow(
    val id: Long,
    val title: String,
    val totalCount: Long,
)

/** The placeholder type a JSON column slide maps to. */
data class MyDataClass(
    val name: String,
)

data class NewTalk(
    val title: String,
    val speakerId: Long,
    val startsAt: Instant,
)

fun ResultRow.toTalkPreview(): TalkPreview = TalkPreview(
    title = this[Talks.title],
    description = this[Talks.description],
    speakerName = this[ProfileTable.name],
    speakerAvatarUrl = this[ProfileTable.avatarUrl],
)

fun ResultRow.toTalkWithSpeakerId(): TalkWithSpeakerId = TalkWithSpeakerId(
    id = this[Talks.id].value,
    title = this[Talks.title],
    slug = this[Talks.slug],
    speakerId = this[Talks.speakerId].value,
    startsAt = this[Talks.startsAt],
)

fun ResultRow.toTalkWithoutSpeaker(): TalkWithoutSpeaker = TalkWithoutSpeaker(
    id = this[Talks.id].value,
    title = this[Talks.title],
    slug = this[Talks.slug],
    startsAt = this[Talks.startsAt],
)

fun String.toUniqueSlug(): String = lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-')

// ---------------------------------------------------------------------------
// SQL helpers defined on one slide and used on the next

/** `COUNT(*) OVER ()`: the total row count next to every row of a page. */
object TotalCount : ExpressionWithColumnType<Long>() {
    override val columnType = LongColumnType()
    override fun toQueryBuilder(queryBuilder: QueryBuilder) {
        queryBuilder.append("COUNT(*) OVER ()")
    }
}

/** PostgreSQL's `unaccent(text)`. */
fun <T : String?> Expression<T>.unaccent() = CustomStringFunction("unaccent", this)

val normalizedName = ProfileTable.name.trim().lowerCase().alias("normalized_name")
val normalizedTitle = Talks.title.lowerCase().unaccent().alias("normalized_title")

data class NormalizedTalk(
    val normalizedName: String,
    val normalizedTitle: String?,
)

// ---------------------------------------------------------------------------
// Values the query fragments refer to

/** The id a `where { Talks.id eq id }` fragment filters on. */
val id: Long = 1L

/** The connection a `transaction(database) { }` fragment runs against. */
val database: Database
    get() = error("The snippets are compiled, not run")
