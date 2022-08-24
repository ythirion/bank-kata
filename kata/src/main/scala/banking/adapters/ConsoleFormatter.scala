package banking.adapters

import banking.domain.{StatementFormatter, Transaction}

import java.lang.System.lineSeparator
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter._
import scala.math.abs

case class ConsoleFormatter() extends StatementFormatter {
  private val dateTimeFormatter: DateTimeFormatter = ofPattern("dd-MM-yyyy")
  private val decimalFormatter: DecimalFormat = new DecimalFormat("#.00")

  private val cellSpace: Int = 8
  private val header: String = "date       |   credit |    debit |  balance"

  override def format(transactions: List[Transaction]): String =
    header + formatTransactions(transactions)

  private def formatTransactions(transactions: List[Transaction]): String =
    if (transactions.isEmpty) ""
    else lineSeparator() + toStatementLines(transactions)

  private def toStatementLines(transactions: List[Transaction]): String =
    transactions.zipWithIndex
      .map {
        case (transaction, index) =>
          toLine(transaction, balanceFor(transactions, index))
      }
      .mkString(lineSeparator())

  private def toLine(
      transaction: Transaction,
      balance: Double
  ): String =
    s"${formatDate(transaction.at)} | ${amountOrEmptyCell(transaction.amount, amount => amount > 0)} | " +
      s"${amountOrEmptyCell(transaction.amount, amount => amount < 0)} | ${formatAmount(balance)}"

  private def balanceFor(transactions: List[Transaction], index: Int): Double =
    transactions
      .drop(index)
      .map(_.amount)
      .sum

  private def amountOrEmptyCell(
      amount: Double,
      shouldBeUsed: Double => Boolean
  ): String =
    if (shouldBeUsed(amount)) formatAmount(abs(amount))
    else emptyCell()

  private def formatAmount(amount: Double): String =
    prefixWithEmptySpace(
      decimalFormatter.format(amount),
      cellSpace
    )

  private def formatDate(date: LocalDateTime): String =
    dateTimeFormatter.format(date)

  private def prefixWithEmptySpace(str: String, targetLength: Int): String =
    s"${generateEmptyString(targetLength - str.length)}$str"

  private def emptyCell(): String = generateEmptyString(cellSpace)

  private def generateEmptyString(length: Int): String =
    (1 to length)
      .map { _ => " " }
      .mkString("")
}
