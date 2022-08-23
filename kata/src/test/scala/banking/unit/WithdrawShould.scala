package banking.unit

import banking.commands.Withdraw
import banking.domain.AccountRepository
import banking.usecases.WithdrawUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

import java.util.UUID

class WithdrawShould
    extends AnyFlatSpec
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {
  private val anAccountId = UUID.randomUUID()
  private val accountRepositoryStub = stub[AccountRepository]
  private val withdrawUseCase = new WithdrawUseCase()

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    withdrawUseCase
      .invoke(Withdraw(anAccountId, 100))
      .left
      .get mustBe "Unknown account"
  }

  private def notExistingAccount(): Unit =
    (accountRepositoryStub.find _)
      .when(anAccountId)
      .returns(None)
}
