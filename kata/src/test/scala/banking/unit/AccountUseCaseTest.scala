package banking.unit

import banking.AccountBuilder
import banking.DataForTests.aLocalDateTime
import banking.domain.{Account, AccountRepository, Clock, Transaction}
import org.scalamock.scalatest.MockFactory
import org.scalatest.EitherValues
import org.scalatest.matchers.must.Matchers

import java.time.LocalDateTime
import java.util.UUID

trait AccountUseCaseTest extends MockFactory with Matchers with EitherValues {
  protected val anAccountId: UUID = UUID.randomUUID()
  protected val accountRepositoryStub: AccountRepository =
    stub[AccountRepository]

  protected val transactionTime: LocalDateTime = aLocalDateTime
  protected val clockStub: Clock = stub[Clock]
  (clockStub.now _).when().returns(transactionTime)

  protected def notExistingAccount(): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(None)

  protected def existingAccount(transactions: Transaction*): Unit =
    existingAccount(transactions.toList)

  protected def existingAccount(transactions: List[Transaction]): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(
        Some(
          AccountBuilder
            .aNewAccount(anAccountId)
            .containing(transactions)
            .build()
        )
      )

  protected def assertErrorForNegativeOrEqualTo0Amount[TCommand](
      commandFactory: Double => TCommand,
      useCase: TCommand => Either[String, Account],
      expectedMessage: String,
      invalidAmounts: Double*
  ): Unit =
    invalidAmounts.foreach { invalidAmount =>
      useCase(commandFactory(invalidAmount)).left.get mustBe expectedMessage
    }

  protected def assertAccountHasBeenCorrectlyUpdated(
      newAccount: Either[String, Account],
      expectedTransactions: List[Transaction]
  ): Unit = {
    newAccount.right.value.transactions mustBe expectedTransactions
    (accountRepositoryStub.save _)
      .verify(newAccount.right.value)
      .once()
  }
}
