package org.example

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.jetbrains.exposed.v1.money.compositeMoney
import java.math.BigDecimal
import javax.money.Monetary
import javax.money.MonetaryAmount

/**
 * A table using exposed-money's composite column support.
 *
 * [price] is represented by two database columns:
 * - `price_amount DECIMAL(19, 2)`
 * - `price_currency VARCHAR(3)`
 */
object MoneyExampleProducts : LongIdTable("money_example_products") {
    val name = varchar("name", 100)
    val price = compositeMoney(
        precision = 19,
        scale = 2,
        amountName = "price_amount",
        currencyName = "price_currency"
    )
}

data class MoneyExampleProduct(
    val id: Long,
    val name: String,
    val price: MonetaryAmount
)

/** Creates a JSR-354 monetary value accepted by an exposed-money column. */
fun money(amount: String, currencyCode: String): MonetaryAmount =
    Monetary.getDefaultAmountFactory()
        .setNumber(BigDecimal(amount))
        .setCurrency(currencyCode)
        .create()

/**
 * Creates the example table, then demonstrates inserting, updating, and reading money values.
 * The caller supplies an already configured Exposed [database].
 */
fun exposedMoneyExample(database: Database): List<MoneyExampleProduct> = transaction(database) {
    SchemaUtils.create(MoneyExampleProducts)

    val coffeeId = MoneyExampleProducts.insertAndGetId {
        it[name] = "Coffee"
        it[price] = money("3.50", "EUR")
    }

    MoneyExampleProducts.update({ MoneyExampleProducts.id eq coffeeId }) {
        it[price] = money("3.75", "EUR")
    }

    MoneyExampleProducts.selectAll().map(::toMoneyExampleProduct)
}

private fun toMoneyExampleProduct(row: ResultRow) = MoneyExampleProduct(
    id = row[MoneyExampleProducts.id].value,
    name = row[MoneyExampleProducts.name],
    price = row[MoneyExampleProducts.price]
)
