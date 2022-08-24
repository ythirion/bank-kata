package banking.unit

import banking.adapters.ConsoleFormatter
import banking.commands.PrintStatement
import banking.domain.StatementFormatter
import banking.usecases.{PrintStatementUseCase, WithdrawUseCase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{EitherValues, OneInstancePerTest}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class PrintStatementShould
    extends AnyFlatSpec
    with AccountUseCaseTest
    with MockFactory
    with EitherValues
    with Matchers
    with OneInstancePerTest {

  private val printerStub = stubFunction[String, Unit]
  private val statementFormatter = ConsoleFormatter()
  private val printStatementUseCase =
    new PrintStatementUseCase(
      accountRepositoryStub,
      printerStub,
      statementFormatter
    )

  it should "return a failure for a non existing account" in {
    notExistingAccount()
    printStatementUseCase
      .invoke(PrintStatement(anAccountId))
      .left
      .get mustBe "Unknown account"
  }

  it should "print an empty statement (only headers) for an existing account without transactions" in {
    existingAccount()

    printStatementUseCase
      .invoke(PrintStatement(anAccountId))
      .isRight mustBe true

    printerStub.verify("date       |   credit |    debit |  balance").once()
  }
}
