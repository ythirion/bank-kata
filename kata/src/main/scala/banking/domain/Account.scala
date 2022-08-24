package banking.domain

import java.time.ZoneOffset
import java.util.UUID

case class Account(id: UUID, transactions: List[Transaction] = List()) {
  def deposit(clock: Clock, amount: Double): Either[String, Account] =
    validateAmount(amount, "Invalid amount for deposit") { validAmount =>
      addTransaction(clock, validAmount)
    }

  def withdraw(clock: Clock, amount: Double): Either[String, Account] = {
    validateAmount(amount, "Invalid amount for withdraw") { validAmount =>
      if (!containsEnoughMoney(validAmount))
        Left(s"Not enough money to withdraw $validAmount")
      else addTransaction(clock, -validAmount)
    }
  }

  def toStatement(statementFormatter: StatementFormatter): String =
    statementFormatter.format(
      transactions.sortBy(_.at.toEpochSecond(ZoneOffset.UTC))
    )

  private def validateAmount(amount: Double, invalidAmountMessage: String)(
      onValidAmount: Double => Either[String, Account]
  ): Either[String, Account] =
    if (!isValidAmount(amount)) Left(invalidAmountMessage)
    else onValidAmount(amount)

  private def isValidAmount(amount: Double) = amount > 0

  private def containsEnoughMoney(amount: Double): Boolean =
    transactions.map(_.amount).sum >= amount

  private def addTransaction(clock: Clock, validAmount: Double) = {
    Right(
      copy(transactions =
        Transaction(clock.now(), validAmount) :: transactions
      )
    )
  }
}
