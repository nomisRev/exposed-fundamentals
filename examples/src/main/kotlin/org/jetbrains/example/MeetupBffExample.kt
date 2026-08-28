package org.jetbrains.example

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Instant
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.core.dao.id.*
import org.jetbrains.exposed.v1.core.statements.InsertStatement
import org.jetbrains.exposed.v1.core.statements.UpsertStatement
import org.jetbrains.exposed.v1.datetime.*
import org.jetbrains.exposed.v1.jdbc.*
import org.jetbrains.exposed.v1.jdbc.transactions.*
import org.jetbrains.exposed.v1.json.*
import kotlin.time.Duration

/** A deliberately small, but complete, meetup schema for a BFF/API layer. */
object ProfileTable : IdTable<Long>() {
  override val id = long("id").autoIncrement().entityId()
  override val primaryKey = PrimaryKey(id)

  val displayName = varchar("display_name", 120)
  val email = varchar("email", 320).uniqueIndex()
  val biography = text("biography").default("")
  val isActive = bool("is_active").default(true)

  val x = array(
    "previews",
    columnType = JsonBColumnType(
      { Json.encodeToString(TalkPreview.serializer(), it) },
      { Json.decodeFromString(TalkPreview.serializer(), it) })
  )
}

fun ResultRow.toTalkPreview() = TalkPreview(
  title = this.get(Talks.title),
  id = TODO(),
  slug = TODO(),
  startsAt = TODO(),
  speaker = TODO(),
  tags = TODO(),
)

object Tags : LongIdTable("tags") {
  val name = varchar("name", 50).uniqueIndex()
  val slug = varchar("slug", 50).uniqueIndex()
}

enum class TalkStatus { DRAFT, PUBLISHED, CANCELLED }

object Talks : LongIdTable("talks") {
  val speakerId = reference("speaker_id", ProfileTable, onDelete = ReferenceOption.RESTRICT)
  val hostId = reference("host_id", ProfileTable, onDelete = ReferenceOption.RESTRICT)
  val title = varchar("title", 200)
  val slug = varchar("slug", 220).uniqueIndex()
  val description = text("description")
  val status = enumerationByName<TalkStatus>("status", 16).default(TalkStatus.DRAFT)

  /** A precise instant, not a transport-specific epoch-millisecond Long. */
  val startsAt = timestamp("starts_at").index()
  val duration = duration("duration_nanoseconds")
  val isFeatured = bool("is_featured").default(false).index()

  val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
  val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)

  init {
    index("idx_talks_status_starts_at", false, status, startsAt)
  }
}

/** Join table: its compound primary key prevents assigning the same tag twice. */
object TalkTags : Table("talk_tags") {
  val talkId = reference("talk_id", Talks, onDelete = ReferenceOption.CASCADE)
  val tagId = reference("tag_id", Tags, onDelete = ReferenceOption.RESTRICT)
  override val primaryKey = PrimaryKey(talkId, tagId)
}

/** A person can bookmark a talk once; deleting either side removes the bookmark. */
object Bookmarks : Table("bookmarks") {
  val personId = reference("person_id", ProfileTable, onDelete = ReferenceOption.CASCADE)
  val talkId = reference("talk_id", Talks, onDelete = ReferenceOption.CASCADE)
  val note = varchar("note", 500).nullable()
  val createdAt = timestamp("created_at")
  override val primaryKey = PrimaryKey(personId, talkId)
}

data class CreateTalk(
  val speakerId: Long,
  val hostId: Long,
  val title: String,
  val slug: String,
  val description: String,
  val startsAt: Instant,
  val duration: Duration,
  val tagIds: List<Long> = emptyList(),
)

data class TalkFilter(
  val query: String? = null,
  val tagSlugs: Set<String> = emptySet(),
  val speakerIds: Set<Long> = emptySet(),
  val startsAtOrAfter: Instant? = null,
  val startsBefore: Instant? = null,
  val featuredOnly: Boolean = false,
  val excludeTalkIds: Set<Long> = emptySet(),
)

enum class TalkSort { STARTS_AT, TITLE }
data class Page(val limit: Int = 20, val offset: Long = 0)

@Serializable
data class TalkPreview(
  val id: Long,
  val title: String,
  val slug: String,
  val startsAt: Instant,
  val speaker: String,
  val tags: List<String>
)

data class TalkDetails(
  val preview: TalkPreview,
  val description: String,
  val host: String,
  val duration: Duration,
  val bookmarkedByViewer: Boolean
)

/**
 * JDBC BFF repository. Every public operation has a short transaction and returns API DTOs,
 * never Exposed ResultRows. Use this class after configuring [database] at application startup.
 */
class MeetupBff(private val database: Database? = null) {
  fun createSchema() = transaction(database) {
    SchemaUtils.create(ProfileTable, Tags, Talks, TalkTags, Bookmarks)
  }

  fun createTalk(input: CreateTalk): Long = transaction(database) {
    val talkId = Talks.insertAndGetId {
      it[speakerId] = input.speakerId
      it[hostId] = input.hostId
      it[title] = input.title
      it[slug] = input.slug
      it[description] = input.description
      it[startsAt] = input.startsAt
      it[duration] = input.duration
    }.value
    replaceTags(talkId, input.tagIds)
    talkId
  }

  /** Frontpage: featured published talks, with deterministic pagination and no SELECT *. */
  fun frontpage(page: Page = Page()): List<TalkPreview> = let {
    val rows = talkQuery(TalkFilter(featuredOnly = true))
      .orderBy(Talks.startsAt to SortOrder.ASC, Talks.id to SortOrder.ASC)
      .limit(page.limit.coerceIn(1, 100)).offset(page.offset)
      .toList()
    previews(rows)
  }

  /** Filtered browse endpoint. Tag filtering uses an IN subquery instead of loading IDs into SQL strings. */
  fun findTalks(filter: TalkFilter, sort: TalkSort = TalkSort.STARTS_AT, page: Page = Page()): List<TalkPreview> =
    transaction(database) {
      val order = if (sort == TalkSort.TITLE) Talks.title to SortOrder.ASC else Talks.startsAt to SortOrder.ASC
      val rows = talkQuery(filter).orderBy(order, Talks.id to SortOrder.ASC)
        .limit(page.limit.coerceIn(1, 100)).offset(page.offset)
        .toList()
      previews(rows)
    }

  fun talkDetails(talkId: Long, viewerId: Long?): TalkDetails? = transaction(database) {
    val row = Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
      .select(
        Talks.id,
        Talks.hostId,
        Talks.title,
        Talks.slug,
        Talks.startsAt,
        Talks.description,
        Talks.duration,
        ProfileTable.displayName
      )
      .where { (Talks.id eq talkId) and (Talks.status eq TalkStatus.PUBLISHED) }
      .singleOrNull() ?: return@transaction null
    val host =
      ProfileTable.select(ProfileTable.displayName).where { ProfileTable.id eq row[Talks.hostId] }
        .single()[ProfileTable.displayName]
    val tagged = tagsFor(listOf(talkId))[talkId].orEmpty()
    val saved = viewerId != null && Bookmarks.select(Bookmarks.talkId)
      .where { (Bookmarks.personId eq viewerId) and (Bookmarks.talkId eq talkId) }.empty().not()
    TalkDetails(
      TalkPreview(
        row[Talks.id].value,
        row[Talks.title],
        row[Talks.slug],
        row[Talks.startsAt],
        row[ProfileTable.displayName],
        tagged
      ), row[Talks.description], host, row[Talks.duration], saved
    )
  }

  fun publishTalk(talkId: Long): Int = transaction(database) {
    Talks.update({ Talks.id eq talkId }) { it[status] = TalkStatus.PUBLISHED }
  }

  /** One UPDATE statement for a selected collection (a bulk/batch state transition). */
  fun featureTalks(talkIds: Collection<Long>, featured: Boolean): Int = transaction(database) {
    if (talkIds.isEmpty()) 0 else Talks.update({ Talks.id inList talkIds.toList() }) { it[isFeatured] = featured }
  }

  fun updateTalkDescription(talkId: Long, title: String, description: String): Int = transaction(database) {
    Talks.update({ Talks.id eq talkId }) { it[Talks.title] = title; it[Talks.description] = description }
  }

  /** Batch upsert is useful for an admin endpoint that imports/renames the tag catalogue. */
  fun upsertTags(tags: List<Pair<String, String>>) = transaction(database) {
    Tags.batchUpsert(tags, Tags.slug, shouldReturnGeneratedValues = false) { (name, slug) ->
      this[Tags.name] = name
      this[Tags.slug] = slug
    }
  }

  fun replaceTags(talkId: Long, tagIds: List<Long>) {
    TalkTags.deleteWhere { TalkTags.talkId eq talkId }
    TalkTags.batchInsert(tagIds.distinct(), shouldReturnGeneratedValues = false) { tagId ->
      this[TalkTags.talkId] = talkId
      this[TalkTags.tagId] = tagId
    }
  }

  /** The compound bookmark primary key is the conflict target for this idempotent command. */
  fun saveBookmark(personId: Long, talkId: Long, note: String?, now: Instant): Unit = transaction(database) {
    Bookmarks.upsert(Bookmarks.personId, Bookmarks.talkId) {
      it[Bookmarks.personId] = personId
      it[Bookmarks.talkId] = talkId
      it[Bookmarks.note] = note
      it[Bookmarks.createdAt] = now
    }
  }

  fun removeBookmark(personId: Long, talkId: Long): Int = transaction(database) {
    Bookmarks.deleteWhere { (Bookmarks.personId eq personId) and (Bookmarks.talkId eq talkId) }
  }

  /** Deleting a talk cascades to tags and bookmarks at the database level. */
  fun deleteTalk(talkId: Long): Int = transaction(database) { Talks.deleteWhere { Talks.id eq talkId } }

  private fun talkQuery(filter: TalkFilter) = Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
    .select(Talks.id, Talks.title, Talks.slug, Talks.startsAt, ProfileTable.displayName)
    .where { Talks.status eq TalkStatus.PUBLISHED }
    .apply {
      filter.query?.takeIf(String::isNotBlank)
        ?.let { term -> andWhere { (Talks.title like "%$term%") or (Talks.description like "%$term%") } }
      if (filter.speakerIds.isNotEmpty()) andWhere { Talks.speakerId inList filter.speakerIds.toList() }
      filter.startsAtOrAfter?.let { andWhere { Talks.startsAt greaterEq it } }
      filter.startsBefore?.let { andWhere { Talks.startsAt lessEq it } }
      if (filter.featuredOnly) andWhere { Talks.isFeatured eq true }
      if (filter.excludeTalkIds.isNotEmpty()) andWhere { Talks.id notInList filter.excludeTalkIds.toList() }
      if (filter.tagSlugs.isNotEmpty()) {
        val taggedTalks =
          TalkTags.innerJoin(Tags).select(TalkTags.talkId).where { Tags.slug inList filter.tagSlugs.toList() }
        andWhere { Talks.id inSubQuery taggedTalks }
      }
    }

  /** Load all tags for a page in one query; mapping remains inside the transaction. */
  private fun previews(rows: List<ResultRow>): List<TalkPreview> {
    val tagsByTalk = tagsFor(rows.map { it[Talks.id].value })
    return rows.map { row ->
      val id = row[Talks.id].value
      TalkPreview(
        id,
        row[Talks.title],
        row[Talks.slug],
        row[Talks.startsAt],
        row[ProfileTable.displayName],
        tagsByTalk[id].orEmpty()
      )
    }
  }

  private fun tagsFor(talkIds: List<Long>): Map<Long, List<String>> =
    if (talkIds.isEmpty()) emptyMap() else TalkTags.innerJoin(Tags).select(TalkTags.talkId, Tags.slug)
      .where { TalkTags.talkId inList talkIds }
      .groupBy({ it[TalkTags.talkId].value }, { it[Tags.slug] })
}
//
//fun update(talkId: Long, status: TalkStatus): TalkPreview? =
//  Talks.updateReturning([Talks.speakerId, Talks.title, Talks.startsAt], { Talks.id eq talkId }) {
//    it[Talks.status] = TalkStatus.PUBLISHED
//  }.singleOrNull()?.toTalkPreview()
//
//fun upsert(talkId: Long, status: TalkStatus): UpsertStatement<Long> =
//  Talks.upsert {
//    it[Talks.status] = status
//  }

fun main() {

  Talks.insertIgnore {  }
  Talks.insertIgnoreAndGetId {  }
  Talks.insertReturning([Talks.id], ignoreErrors = true) {  }

  Talks.deleteReturning { Talks.id eq 1 }

  Talks.upsert(Talks.slug) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }

  Talks.upsertReturning(Talks.slug, returning = [Talks.title, Talks.description]) {
    it[Talks.slug] = slug
    it[Talks.title] = title
    it[Talks.speakerId] = speakerId
    it[Talks.startsAt] = startsAt
  }


  Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  Talks.rightJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  Talks.leftJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  Talks.fullJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
  Talks.crossJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }

  Talks.select(Talks.speakerId, Talks.title, Talks.startsAt)
    .where { Talks.id eq 1 or (Talks.isFeatured eq true) }
    .orWhere { Talks.isFeatured eq true }
    .where { Talks.id inList [1, 2, 3] }
    .andWhere { Talks.description like "%Kotlin%" }
    .orderBy(Talks.title, SortOrder.ASC)
    .limit(10)
    .offset(30)
    .having { Talks.description like "%Kotlin%" }
    .andHaving { Talks.isFeatured eq true }
    .orHaving { Talks.isFeatured eq true }
    .withDistinct(true)
    .withDistinctOn(Talks.speakerId)
    .adjustSelect { selectAll() }

//    Database.connect(
//        url = "jdbc:postgresql://localhost:5432/example",
//        user = "postgresql",
//        password = "password",
//        databaseConfig = DatabaseConfig {
//            sqlLogger = Slf4jSqlDebugLogger
//            defaultMaxAttempts = 1
//        }
//    )
//
//    R2dbcDatabase.connect(
//        url = "r2dbc:postgresql://localhost:5432/example",
//        user = "postgresql",
//        password = "password",
//        databaseConfig = R2dbcDatabaseConfig {
//            sqlLogger = Slf4jSqlDebugLogger
//            defaultMaxAttempts = 1
//            defaultR2dbcIsolationLevel = IsolationLevel.READ_COMMITTED
//        }
//    )
//
//    context(_: Transaction)
//    fun previews() =
//        Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
//            .select(Talks.id, Talks.title, Talks.slug)
//
//    transaction { previews() }
//
//    data class Draft(val title: String, val speakerId: Long)
//
//    transaction {
//        val inserted = insert("title", 1L, Clock.System.now())
//
//        val statement = InsertStatement<Number>(Talks)
//        statement[Talks.title] ="Exposed"
//        statement[Talks.description] = "Introduction"
//        statement.toExecutable().execute(this)
//    }

}

//
//fun String.toUniqueSlug() = replace(" ", "-").lowercase()
//
//context(_: Transaction)
//fun insert(title: String, speakerId: Long, startsAt: Instant): InsertStatement<Number> =
//  Talks.insert {
//    it[Talks.title] = title
//    it[Talks.slug] = title.toUniqueSlug()
//    it[Talks.speakerId] = speakerId
//    it[Talks.startsAt] = startsAt
//  }
//
//context(_: Transaction)
//fun insertAndGetId(title: String, speakerId: Long, startsAt: Instant): EntityID<Long> =
//  Talks.insertAndGetId {
//    it[Talks.title] = title
//    it[Talks.slug] = title.toUniqueSlug()
//    it[Talks.speakerId] = speakerId
//    it[Talks.startsAt] = startsAt
//  }
//
//context(_: Transaction)
//fun insertReturning(title: String, speakerId: Long, startsAt: Instant): List<TalkWithoutSpeaker> =
//  Talks.insertReturning {
//    it[Talks.title] = title
//    it[Talks.speakerId] = speakerId
//    it[Talks.startsAt] = startsAt
//  }.map(ResultRow::toTalkWithoutSpeaker)
//
//
//data class NewTalk(val title: String, val description: String, val speakerId: Long, val startsAt: Instant)
//
//context(_: Transaction)
//fun insertBatch(newTalk: List<NewTalk>): List<TalkWithoutSpeaker> =
//  Talks.batchInsert(newTalk) { newTalk ->
//    this[Talks.title] = newTalk.title
//    this[Talks.speakerId] = newTalk.speakerId
//    this[Talks.startsAt] = newTalk.startsAt
//  }.map(ResultRow::toTalkWithoutSpeaker)
//
//context(_: Transaction)
//fun insertBatch_(newTalk: List<NewTalk>) {
//  val batch = Talks.batchInsert(newTalk, shouldReturnGeneratedValues = false) { newTalk ->
//    this[Talks.title] = newTalk.title
//    this[Talks.speakerId] = newTalk.speakerId
//    this[Talks.startsAt] = newTalk.startsAt
//  }
//
//}
//
//fun ResultRow.toTalkWithoutSpeaker(): TalkWithoutSpeaker = TODO()
//
//data class TalkWithoutSpeaker(
//  val id: Long,
//  val title: String,
//  val slug: String,
//  val speakerId: Long,
//  val startsAt: Instant,
//  val createdAt: Instant,
//  val updatedAt: Instant
//)
