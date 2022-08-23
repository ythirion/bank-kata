package banking.unit

import banking.AccountBuilder
import banking.domain.{AccountRepository, Transaction}
import org.scalamock.scalatest.MockFactory

import java.util.UUID

trait AccountUseCaseTest extends MockFactory {
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
}
