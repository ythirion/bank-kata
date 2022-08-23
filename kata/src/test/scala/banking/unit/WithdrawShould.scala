package banking.unit

import banking.TransactionBuilder.aNewTransaction
import banking.commands.Withdraw
import banking.domain.Transaction
import banking.usecases.WithdrawUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

import scala.Double.NegativeInfinity

class WithdrawShould
    extends AnyFlatSpec
    with AccountUseCaseTest
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {
  private val withdrawUseCase =
    new WithdrawUseCase(accountRepositoryStub, clockStub)

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    withdrawUseCase
      .invoke(createWithdrawCommand(100))
      .left
      .get mustBe "Unknown account"
  }

  it should "return a failure for an existing account and a withdraw of <= 0" in {
    existingAccount()

    assertErrorForNegativeOrEqualTo0Amount[Withdraw](
      invalidAmount => createWithdrawCommand(invalidAmount),
      withdraw => withdrawUseCase.invoke(withdraw),
      "Invalid amount for withdraw",
      0,
      -1,
      -1000,
      NegativeInfinity
    )
  }

  it should "return a failure because not enough money available for an existing account with not enough money & a withdraw of 100" in {
    existingAccount()

    withdrawUseCase
      .invoke(createWithdrawCommand(100))
      .left
      .get mustBe "Not enough money to withdraw 100.0"
  }

  it should "store the updated account containing a Transaction(transactionTime, -900) for an existing account containing enough money to withdraw" in {
    val formerTransactions =
      List(aNewTransaction().of(500).build(), aNewTransaction().of(500).build())

    existingAccount(formerTransactions)

    val newAccount = withdrawUseCase.invoke(createWithdrawCommand(900))

    assertAccountHasBeenCorrectlyUpdated(
      newAccount,
      Transaction(
        transactionTime,
        -900
      ) :: formerTransactions
    )
  }

  it should "be able to withdraw the whole account amount" in {
    val formerTransaction = aNewTransaction().of(1000).build()

    existingAccount(formerTransaction)

    val newAccount =
      withdrawUseCase.invoke(createWithdrawCommand(formerTransaction.amount))

    assertAccountHasBeenCorrectlyUpdated(
      newAccount,
      List(
        Transaction(
          transactionTime,
          -1000
        ),
        formerTransaction
      )
    )
  }

  private def createWithdrawCommand(amount: Double) =
    Withdraw(anAccountId, amount)
}
