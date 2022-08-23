package banking.unit

import banking.AccountBuilder
import banking.domain.{Account, AccountRepository, Transaction}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.must.Matchers

import java.util.UUID

trait AccountUseCaseTest extends MockFactory with Matchers {
  protected val anAccountId: UUID = UUID.randomUUID()
  protected val accountRepositoryStub: AccountRepository =
    stub[AccountRepository]

  protected def notExistingAccount(): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(None)

  protected def existingAccount(transactions: Transaction*): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(
        Some(
          AccountBuilder
            .aNewAccount(anAccountId)
            .containing(transactions.toList)
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
}
