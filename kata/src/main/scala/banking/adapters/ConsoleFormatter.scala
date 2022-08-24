package banking.adapters

import banking.domain.{StatementFormatter, Transaction}

import java.lang.System.lineSeparator
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter._

case class ConsoleFormatter() extends StatementFormatter {
  private val dateTimeFormatter: DateTimeFormatter = ofPattern("dd-MM-yyyy")
  private val decimalFormatter: DecimalFormat = new DecimalFormat("#.00")
  private val cellSpace: Int = 8

  private val header: String = "date       |   credit |    debit |  balance"

  override def format(transactions: List[Transaction]): String =
    header + lineSeparator() + formatTransactions(transactions)

  private def formatTransactions(transactions: List[Transaction]): String =
    transactions
      .map(transaction => formatLine(transaction, 0))
      .mkString(lineSeparator())

  private def formatLine(
      transaction: Transaction,
      currentBalance: Double
  ): String =
    s"${formatDate(transaction.at)} | ${formatAmount(transaction.amount)} | $currentBalance"

  private def formatDate(date: LocalDateTime): String =
    dateTimeFormatter.format(date)

  private def formatAmount(amount: Double) =
    prefixWithEmptySpace(
      decimalFormatter.format(amount),
      cellSpace
    )

  private def prefixWithEmptySpace(str: String, targetLength: Int): String =
    s"${generateEmptyString(targetLength - str.length)}$str"

  private def generateEmptyString(length: Int): String =
    (1 to length)
      .map { _ => " " }
      .mkString("")
}
