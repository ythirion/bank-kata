package banking.usecases

import banking.commands.Withdraw
import banking.domain.Account

class WithdrawUseCase() {
  def invoke(withdraw: Withdraw): Either[String, Account] =
    Left("Unknown account")
}
