package banking.unit

import banking.commands.Withdraw
import banking.usecases.WithdrawUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

class WithdrawShould
    extends AnyFlatSpec
    with AccountUseCaseTest
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {
  private val withdrawUseCase = new WithdrawUseCase()

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    withdrawUseCase
      .invoke(Withdraw(anAccountId, 100))
      .left
      .get mustBe "Unknown account"
  }
}
