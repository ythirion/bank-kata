package banking.adapters

import banking.domain.{StatementFormatter, Transaction}

case class ConsoleFormatter() extends StatementFormatter {
  override def format(transactions: List[Transaction]): String =
    "date       |   credit |    debit |  balance"
}
