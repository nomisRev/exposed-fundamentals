package org.example.r2dbc

import org.jetbrains.exposed.v1.core.CustomFunction
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.dao.id.UuidTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.innerJoin
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.timestamp
import org.jetbrains.exposed.v1.r2dbc.Query
import org.jetbrains.exposed.v1.r2dbc.andWhere
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.select
import org.jetbrains.exposed.v1.money.CompositeMoneyColumn
import org.jetbrains.exposed.v1.money.compositeMoney
import java.math.BigDecimal
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryAmount

object ProfileTable : LongIdTable() {
    val name = varchar("name", 200)
    val biography = text("biography")
}

@JvmInline
value class TalkTitle(val value: String)


object Talks : LongIdTable(name = "talks", columnName = "talks_id", sequenceName = "talks_id_seq") {
    val x = long("k").autoIncrement()
    val speakerId = reference(
        "speaker_id",
        ProfileTable.id,
        onDelete = ReferenceOption.RESTRICT,
        onUpdate = ReferenceOption.CASCADE,
        fkName = "fk_talks_speaker_id"
    )

    //    val title = varchar("title", 200)
    val description = text("description")
    val isPublished = bool("is_published").default(false)

    val title = varchar("title", 200)
        .transform(::TalkTitle) { it.value }

    val date = datetime("date")
    val money: CompositeMoneyColumn<BigDecimal, CurrencyUnit, MonetaryAmount> = compositeMoney(1, 2, "money")

    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val updatedAt = timestamp("updated_at").databaseGenerated()
}

suspend fun test() {
    val value: MonetaryAmount = Monetary.getDefaultAmountFactory()
        .setNumber(BigDecimal(1))
        .setCurrency("EUR")
        .create()

    val talkTitleWithSpeakerName: Query =
        Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
            .select(Talks.title, ProfileTable.name)
            .where { Talks.isPublished eq true }

    fun talks(): Query =
        Talks.innerJoin(ProfileTable) { Talks.speakerId eq ProfileTable.id }
            .select(Talks.title, ProfileTable.name)
            .where { Talks.isPublished eq true }

    val secondPage = talkTitleWithSpeakerName
        .orderBy(Talks.title, SortOrder.ASC)
        .limit(20)
        .offset(20)

    fun secondPage(): Query = talks()
        .orderBy(
            Talks.title to SortOrder.ASC,
            Talks.description to SortOrder.DESC
        )
        .limit(20)
        .offset(20)

    val kotlinTalks = talkTitleWithSpeakerName
        .andWhere { Talks.description like "%Kotlin%" }
        .adjustSelect {
            select(Talks.title, Talks.description, ProfileTable.name, ProfileTable.biography)
        }


    val x = Talks.insert {
        it[money] = value
    }
}

object ProfileTable2 : UuidTable("profiles2") {
    init {
        id.defaultExpression(CustomFunction("uuidv7()", id.columnType))
    }
}

fun main() {
    println(ProfileTable.tableName)
}

