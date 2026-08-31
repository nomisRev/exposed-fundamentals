package org.jetbrains.exposed.example.jdbc

import kotlin.uuid.Uuid
import org.jetbrains.exposed.v1.core.JoinType
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.alias
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.count
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inSubQuery
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.Query
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.JdbcTransaction
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.dao.UuidEntity
import org.jetbrains.exposed.v1.dao.UuidEntityClass
import org.jetbrains.exposed.v1.dao.load
import org.jetbrains.exposed.v1.dao.with
import org.jetbrains.exposed.example.Bookmarks
import org.jetbrains.exposed.example.NewTalk
import org.jetbrains.exposed.example.ProfileTable
import org.jetbrains.exposed.example.Tags
import org.jetbrains.exposed.example.TalkPreview
import org.jetbrains.exposed.example.TalkTags
import org.jetbrains.exposed.example.Talks

fun connectJdbc(): Database = Database.connect(
    url = "jdbc:postgresql://localhost:5432/example",
    user = "postgres",
    password = "password",
)

/** Queries execute while the explicit transaction is active and return detached application data. */
fun publishedTalks(database: Database): List<TalkPreview> = transaction(database) {
    publishedTalkPreviews().map(ResultRow::toTalkPreview)
}

fun JdbcTransaction.publishedTalkPreviews(): Query =
    Talks.innerJoin(ProfileTable)
        .select(
            Talks.id,
            Talks.title,
            Talks.description,
            Talks.startsAt,
            ProfileTable.name,
            ProfileTable.avatarUrl,
        )
        .where { Talks.isPublished eq true }
        .orderBy(Talks.startsAt to SortOrder.ASC, Talks.title to SortOrder.ASC)

fun ResultRow.toTalkPreview() = TalkPreview(
    id = this[Talks.id].value,
    title = this[Talks.title],
    description = this[Talks.description],
    startsAt = this[Talks.startsAt],
    speakerName = this[ProfileTable.name],
    speakerAvatarUrl = this[ProfileTable.avatarUrl],
)

fun createTalk(database: Database, talk: NewTalk): Uuid = transaction(database) {
    Talks.insertAndGetId {
        it[speakerId] = talk.speakerId
        it[hostId] = talk.hostId
        it[title] = talk.title
        it[slug] = talk.slug
        it[description] = talk.description
        it[startsAt] = talk.startsAt
    }.value
}

fun createTalks(database: Database, talks: List<NewTalk>) = transaction(database) {
    Talks.batchInsert(talks, shouldReturnGeneratedValues = false) { talk ->
        this[Talks.speakerId] = talk.speakerId
        this[Talks.hostId] = talk.hostId
        this[Talks.title] = talk.title
        this[Talks.slug] = talk.slug
        this[Talks.description] = talk.description
        this[Talks.startsAt] = talk.startsAt
    }
}

fun publishTalk(database: Database, talkId: Uuid): Boolean = transaction(database) {
    Talks.update({ Talks.id eq talkId }) {
        it[isPublished] = true
    } == 1
}

fun deleteTalk(database: Database, talkId: Uuid): Boolean = transaction(database) {
    Talks.deleteWhere { Talks.id eq talkId } == 1
}

/** The same profile table is joined twice under distinct speaker and host aliases. */
fun JdbcTransaction.talksWithSpeakerAndHost(): Query {
    val speaker = ProfileTable.alias("speaker")
    val host = ProfileTable.alias("host")

    return Talks
        .innerJoin(speaker, { Talks.speakerId }, { speaker[ProfileTable.id] })
        .innerJoin(host, { Talks.hostId }, { host[ProfileTable.id] })
        .select(Talks.title, speaker[ProfileTable.name], host[ProfileTable.name])
}

/** A left join retains talks with no bookmarks. */
fun JdbcTransaction.talksByBookmarkCount(): Query {
    val bookmarkCount = Bookmarks.talkId.count().alias("bookmark_count")

    return Talks.join(
        Bookmarks,
        JoinType.LEFT,
        additionalConstraint = { Talks.id eq Bookmarks.talkId },
    ).select(Talks.id, Talks.title, bookmarkCount)
        .groupBy(Talks.id, Talks.title)
        .orderBy(bookmarkCount, SortOrder.DESC)
}

fun JdbcTransaction.talkIdsForTag(tag: String): Query =
    TalkTags.innerJoin(Tags)
        .select(TalkTags.talkId)
        .where { Tags.label eq tag }

fun JdbcTransaction.searchTalks(tag: String?, title: String?): Query {
    val query = Talks.selectAll()
    tag?.let { query.andWhere { Talks.id inSubQuery talkIdsForTag(it) } }
    title?.let { query.andWhere { Talks.title like "%$it%" } }
    return query
}

class Profile(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<Profile>(ProfileTable)

    var name by ProfileTable.name
    var biography by ProfileTable.biography
    var avatarUrl by ProfileTable.avatarUrl
    var email by ProfileTable.email
}

class Tag(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<Tag>(Tags)

    var label by Tags.label
}

class Talk(id: EntityID<Uuid>) : UuidEntity(id) {
    companion object : UuidEntityClass<Talk>(Talks)

    var speaker by Profile referencedOn Talks.speakerId
    var host by Profile referencedOn Talks.hostId
    var title by Talks.title
    var slug by Talks.slug
    var description by Talks.description
    var startsAt by Talks.startsAt
    var isPublished by Talks.isPublished
    var tags by Tag via TalkTags
}

/** DAO uses the same tables and remains inside the JDBC transaction boundary. */
fun publishedTalkEntities(database: Database): List<TalkPreview> = transaction(database) {
    Talk.find { Talks.isPublished eq true }
        .with(Talk::speaker)
        .map { talk ->
            TalkPreview(
                id = talk.id.value,
                title = talk.title,
                description = talk.description,
                startsAt = talk.startsAt,
                speakerName = talk.speaker.name,
                speakerAvatarUrl = talk.speaker.avatarUrl,
            )
        }
}
