package banking.acceptance

import banking.adapters.ConsoleFormatter
import banking.commands
import banking.commands.{Deposit, PrintStatement, Withdraw}
import banking.domain.Clock
import banking.usecases.{DepositUseCase, PrintStatementUseCase, WithdrawUseCase}
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

import java.util.UUID

class PrintStatementFeature extends AnyFlatSpec with Matchers with MockFactory {
  behavior of "Account API"

  private val accountId = UUID.randomUUID()
  private val printerStub = stubFunction[String, Unit]
  private val clockStub = stub[Clock]

  private val accountRepositoryFake = new AccountRepositoryFake(accountId)

  private val depositUseCase =
    new DepositUseCase(accountRepositoryFake, clockStub)
  private val withDrawUseCase =
    new WithdrawUseCase(accountRepositoryFake, clockStub)
  private val printStatementUseCase =
    new PrintStatementUseCase(
      accountRepositoryFake,
      printerStub,
      ConsoleFormatter()
    )

  it should "print statement containing all the transactions" in {
    depositUseCase.invoke(Deposit(accountId, 1000d))
    depositUseCase.invoke(commands.Deposit(accountId, 2000d))
    withDrawUseCase.invoke(Withdraw(accountId, 500d))

    printStatementUseCase.invoke(PrintStatement(accountId))

    printerStub
      .verify(
        """date       |   credit |    debit |  balance
          |19-01-2022 |          |   500.00 |  2500.00
          |18-01-2022 |  2000.00 |          |  3000.00
          |12-01-2022 |  1000.00 |          |  1000.00""".stripMargin
      )
      .once()
  }
}
