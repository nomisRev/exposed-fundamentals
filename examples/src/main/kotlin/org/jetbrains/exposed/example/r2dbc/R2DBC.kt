package org.jetbrains.exposed.example.r2dbc

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlin.uuid.Uuid
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.r2dbc.Query
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.batchInsert
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertAndGetId
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.r2dbc.update
import org.jetbrains.exposed.example.NewTalk
import org.jetbrains.exposed.example.ProfileTable
import org.jetbrains.exposed.example.TalkPreview
import org.jetbrains.exposed.example.Talks

fun connectR2dbc(): R2dbcDatabase = R2dbcDatabase.connect(
    url = "r2dbc:postgresql://localhost:5432/example",
    user = "postgres",
    password = "password",
)

/** The query shape mirrors JDBC; only the execution module and effect differ. */
fun publishedTalkPreviews(): Query =
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

suspend fun publishedTalks(database: R2dbcDatabase): List<TalkPreview> =
    suspendTransaction(database) {
        publishedTalkPreviews().map(ResultRow::toTalkPreview).toList()
    }

fun ResultRow.toTalkPreview() = TalkPreview(
    id = this[Talks.id].value,
    title = this[Talks.title],
    description = this[Talks.description],
    startsAt = this[Talks.startsAt],
    speakerName = this[ProfileTable.name],
    speakerAvatarUrl = this[ProfileTable.avatarUrl],
)

suspend fun createTalk(database: R2dbcDatabase, talk: NewTalk): Uuid =
    suspendTransaction(database) {
        Talks.insertAndGetId {
            it[speakerId] = talk.speakerId
            it[hostId] = talk.hostId
            it[title] = talk.title
            it[slug] = talk.slug
            it[description] = talk.description
            it[startsAt] = talk.startsAt
        }.value
    }

suspend fun createTalks(database: R2dbcDatabase, talks: List<NewTalk>) {
    suspendTransaction(database) {
        Talks.batchInsert(talks, shouldReturnGeneratedValues = false) { talk ->
            this[Talks.speakerId] = talk.speakerId
            this[Talks.hostId] = talk.hostId
            this[Talks.title] = talk.title
            this[Talks.slug] = talk.slug
            this[Talks.description] = talk.description
            this[Talks.startsAt] = talk.startsAt
        }
    }
}

suspend fun publishTalk(database: R2dbcDatabase, talkId: Uuid): Boolean =
    suspendTransaction(database) {
        Talks.update({ Talks.id eq talkId }) {
            it[isPublished] = true
        } == 1
    }

suspend fun deleteTalk(database: R2dbcDatabase, talkId: Uuid): Boolean =
    suspendTransaction(database) {
        Talks.deleteWhere { Talks.id eq talkId } == 1
    }
