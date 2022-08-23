package banking.unit

import banking.DataForTests._
import banking.TransactionBuilder.aNewTransaction
import banking.commands.Deposit
import banking.domain.{Account, Clock, Transaction}
import banking.usecases.DepositUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

import java.time.LocalDateTime
import scala.Double._

class DepositShould
    extends AnyFlatSpec
    with AccountUseCaseTest
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {
  private val transactionTime: LocalDateTime = aLocalDateTime
  private val depositOf1000 = createDepositCommand(1000)

  private val clockStub: Clock = stub[Clock]
  (clockStub.now _).when().returns(transactionTime)

  private val depositUseCase =
    new DepositUseCase(accountRepositoryStub, clockStub)

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    depositUseCase.invoke(depositOf1000).left.get mustBe "Unknown account"
  }

  it should "return a failure for an existing account and a deposit of <= 0" in {
    existingAccount()
    assertErrorForNegativeOrEqualTo0Amount[Deposit](
      invalidAmount => createDepositCommand(invalidAmount),
      deposit => depositUseCase.invoke(deposit),
      "Invalid amount for deposit",
      0,
      -1,
      -1000,
      NegativeInfinity
    )
  }

  it should "store the updated account containing a Transaction(transactionTime, 1000) for an existing account and a deposit of 1000" in {
    existingAccount()
    val newAccount = depositUseCase.invoke(depositOf1000)

    assertAccountHasBeenCorrectlyUpdated(
      newAccount,
      List(Transaction(transactionTime, 1000))
    )
  }

  it should "store the updated account containing a Transaction(transactionTime, 1000) for an existing account containing already a Transaction(09/10/1987, -200) and a deposit of 1000" in {
    val anExistingTransaction = aNewTransaction()
      .madeAt(anotherDateTime)
      .of(-200)
      .build()

    existingAccount(anExistingTransaction)

    val newAccount = depositUseCase.invoke(depositOf1000)

    assertAccountHasBeenCorrectlyUpdated(
      newAccount,
      List(
        Transaction(transactionTime, 1000),
        anExistingTransaction
      )
    )
  }

  private def createDepositCommand(amount: Double): Deposit =
    Deposit(anAccountId, amount)

  private def assertAccountHasBeenCorrectlyUpdated(
      newAccount: Either[String, Account],
      expectedTransactions: List[Transaction]
  ): Unit = {
    newAccount.right.value.transactions mustBe expectedTransactions
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }
}
