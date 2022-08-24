package banking.unit

import banking.TransactionBuilder._
import banking.adapters.ConsoleFormatter
import banking.commands.PrintStatement
import banking.usecases.PrintStatementUseCase
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{EitherValues, OneInstancePerTest}

import java.time.LocalDateTime

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

  it should "print a statement in reverse chronological order for an existing account with transactions Existing account with transactions (+20 078.89, -5890.87, +3678.54)" in {
    val formerTransactions = List(
      aNewTransaction()
        .madeAt(LocalDateTime.of(2021, 12, 31, 11, 34))
        .of(20078.89),
      aNewTransaction()
        .madeAt(LocalDateTime.of(2022, 8, 13, 21, 49))
        .of(-5890.87),
      aNewTransaction()
        .madeAt(LocalDateTime.of(2022, 8, 13, 21, 50))
        .of(3678.54)
    )

    existingAccount(
      formerTransactions.map(_.build())
    )

    printStatementUseCase
      .invoke(PrintStatement(anAccountId))
      .isRight mustBe true

    printerStub.verify("""date       |   credit |    debit |  balance
        |13-08-2022 |  3678.54 |          | 17866.56
        |13-08-2022 |          |  5890.87 | 14188.02
        |31-12-2021 | 20078.89 |          | 20078.89""".stripMargin).once()
  }
}
