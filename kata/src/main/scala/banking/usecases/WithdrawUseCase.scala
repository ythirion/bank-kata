package banking.usecases

import banking.commands.Withdraw
import banking.domain.{Account, AccountRepository}

class WithdrawUseCase(accountRepository: AccountRepository) {
  def invoke(withdraw: Withdraw): Either[String, Account] =
    accountRepository.find(withdraw.accountId) match {
      case Some(account) => account.withdraw(withdraw.amount)
      case None          => Left("Unknown account")
    }
}
