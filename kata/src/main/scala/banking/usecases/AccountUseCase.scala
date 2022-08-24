package banking.usecases

import banking.domain.{Account, AccountRepository}

import java.util.UUID

trait AccountUseCase {
  protected def invokeWhenAccountExists[TResult](
      accountRepository: AccountRepository,
      accountId: UUID
  )(onSuccess: Account => Either[String, TResult]): Either[String, TResult] =
    accountRepository.find(accountId) match {
      case Some(account) => onSuccess(account)
      case None          => Left("Unknown account")
    }
}
