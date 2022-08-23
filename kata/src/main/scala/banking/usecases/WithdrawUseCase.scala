package banking.usecases

import banking.commands.Withdraw
import banking.domain.{Account, AccountRepository, Clock}

class WithdrawUseCase(accountRepository: AccountRepository, clock: Clock) {
  def invoke(withdraw: Withdraw): Either[String, Account] =
    accountRepository.find(withdraw.accountId) match {
      case Some(account) => withdrawSafely(withdraw, account)
      case None          => Left("Unknown account")
    }

  private def withdrawSafely(
      withdraw: Withdraw,
      account: Account
  ): Either[String, Account] = {
    account.withdraw(clock, withdraw.amount) match {
      case Right(updatedAccount) =>
        accountRepository.save(updatedAccount)
        Right(updatedAccount)
      case Left(error) => Left(error)
    }
  }
}
