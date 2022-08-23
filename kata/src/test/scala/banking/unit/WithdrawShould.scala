package banking.unit

import banking.commands.Withdraw
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
  private val withdrawUseCase = new WithdrawUseCase(accountRepositoryStub)

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    withdrawUseCase
      .invoke(Withdraw(anAccountId, 100))
      .left
      .get mustBe "Unknown account"
  }

  it should "return a failure for an existing account and a withdraw of <= 0" in {
    existingAccount()

    assertErrorForNegativeOrEqualTo0Amount[Withdraw](
      invalidAmount => Withdraw(anAccountId, invalidAmount),
      withdraw => withdrawUseCase.invoke(withdraw),
      "Invalid amount for withdraw",
      0,
      -1,
      -1000,
      NegativeInfinity
    )
  }
}
