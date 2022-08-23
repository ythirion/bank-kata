package banking.domain

import java.util.UUID

case class Account(id: UUID, transactions: List[Transaction] = List()) {
  def deposit(clock: Clock, amount: Double): Either[String, Account] = {
    if (amount <= 0) Left("Invalid amount for deposit")
    else
      Right(
        copy(transactions = Transaction(clock.now(), amount) :: transactions)
      )
  }

  def withdraw(amount: Double): Either[String, Account] = {
    if (amount <= 0) Left("Invalid amount for withdraw")
    else Left(s"Not enough money to withdraw $amount")
  }
}
