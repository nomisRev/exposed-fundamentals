package org.jetbrains.exposed.example

import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.Table.UuidVersion
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

/** A domain type used by the column-transformation example in the presentation. */
@JvmInline
value class TalkTitle(val value: String)

object ProfileTable : UuidTable(name = "profiles", uuidVersion = UuidVersion.V7) {
    val name = varchar("name", 120)
    val biography = text("biography").default("")
    val avatarUrl = varchar("avatar_url", 500).nullable()
    val email = varchar("email", 320).uniqueIndex()
}

object Talks : UuidTable(name = "talks", uuidVersion = UuidVersion.V7) {
    val speakerId = reference(
        name = "speaker_id",
        foreign = ProfileTable,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
    )
    val hostId = reference(
        name = "host_id",
        foreign = ProfileTable,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
    )
    val title = varchar("title", 200)
    val slug = varchar("slug", 220).uniqueIndex()
    val description = text("description")
    val startsAt = timestamp("starts_at")
    val isPublished = bool("is_published").default(false)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
}

@OptIn(ExperimentalUuidApi::class)
object Tags : UuidTable(name = "tags", uuidVersion = UuidVersion.V7,) {
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

    init {
        index("bookmarks_talk_idx", isUnique = false, talkId)
    }
}

data class NewTalk(
    val speakerId: Uuid,
    val hostId: Uuid,
    val title: String,
    val description: String,
    val startsAt: Instant,
) {
    val slug: String = title.toSlug()
}

data class TalkPreview(
    val id: Uuid,
    val title: String,
    val description: String,
    val startsAt: Instant,
    val speakerName: String,
    val speakerAvatarUrl: String?,
)

fun String.toSlug(): String = lowercase()
    .replace(Regex("[^a-z0-9]+"), "-")
    .trim('-')
