package banking.usecases

import banking.commands.PrintStatement
import banking.domain.{AccountRepository, StatementFormatter}

class PrintStatementUseCase(
    accountRepository: AccountRepository,
    printer: String => Unit,
    statementFormatter: StatementFormatter
) extends AccountUseCase {
  def invoke(printStatement: PrintStatement): Either[String, Unit] =
    invokeWhenAccountExists(
      accountRepository,
      printStatement.accountId,
      account =>
        Right(
          printer(
            account.toStatement(statementFormatter)
          )
        )
    )
}
