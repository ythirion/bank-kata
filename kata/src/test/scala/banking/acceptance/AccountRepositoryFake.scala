package banking.acceptance

import banking.AccountBuilder
import banking.domain.{Account, AccountRepository}

import java.util.UUID
import scala.collection.mutable

class AccountRepositoryFake(val existingAccounts: UUID*)
    extends AccountRepository {

  private val inMemoryAccounts: mutable.Map[UUID, Account] =
    existingAccounts
      .map(id => id -> AccountBuilder.aNewAccount(id).build())
      .toMap
      .to(mutable.Map[UUID, Account])

  override def find(accountId: UUID): Option[Account] =
    inMemoryAccounts.get(accountId)

  override def save(account: Account): Unit =
    inMemoryAccounts.update(account.id, account)
}
