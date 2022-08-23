package banking.domain

import java.util.UUID

case class Account(id: UUID, transactions: List[Transaction] = List()) {
  def deposit(clock: Clock, amount: Double): Either[String, Account] =
    validateAmount(amount, "Invalid amount for deposit") { validAmount =>
      Right(
        copy(transactions =
          Transaction(clock.now(), validAmount) :: transactions
        )
      )
    }

  def withdraw(amount: Double): Either[String, Account] = {
    validateAmount(amount, "Invalid amount for withdraw") { validAmount =>
      Left(s"Not enough money to withdraw $validAmount")
    }
  }

  private def validateAmount(amount: Double, invalidAmountMessage: String)(
      onValidAmount: Double => Either[String, Account]
  ): Either[String, Account] =
    if (isValidAmount(amount)) onValidAmount(amount)
    else Left(invalidAmountMessage)

  private def isValidAmount(amount: Double) = amount > 0
}
