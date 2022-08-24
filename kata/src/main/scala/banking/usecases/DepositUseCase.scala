package banking.usecases

import banking.commands.Deposit
import banking.domain.{Account, AccountRepository, Clock}

class DepositUseCase(accountRepository: AccountRepository, clock: Clock)
    extends AccountUseCase {
  def invoke(deposit: Deposit): Either[String, Account] =
    invokeWhenAccountExists(accountRepository, deposit.accountId) { account =>
      depositSafely(deposit, account)
    }

  private def depositSafely(
      deposit: Deposit,
      account: Account
  ): Either[String, Account] =
    account.deposit(clock, deposit.amount) match {
      case Right(updatedAccount) =>
        accountRepository.save(updatedAccount)
        Right(updatedAccount)
      case Left(error) => Left(error)
    }
}
