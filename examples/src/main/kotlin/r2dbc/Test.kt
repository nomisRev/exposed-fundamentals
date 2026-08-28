package r2dbc

import org.example.r2dbc.ProfileTable
import org.example.r2dbc.Talks
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.r2dbc.Query

fun talks(): Query =
    Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
        .select(Talks.title, ProfileTable.name)
        .where { Talks.isPublished eq true }

fun secondPage(): Query = talks()
    .orderBy(
        Talks.title to SortOrder.ASC,
        Talks.description to SortOrder.DESC
    )
    .limit(20)
    .offset(20)

fun kotlinTalks() = talks()
    .andWhere { Talks.description like "%Kotlin%" }
    .adjustSelect {
        select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.biography)
    }
