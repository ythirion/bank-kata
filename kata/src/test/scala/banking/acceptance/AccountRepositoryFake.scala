package banking.acceptance

import banking.AccountBuilder._
import banking.domain.{Account, AccountRepository}

import java.util.UUID
import scala.collection.mutable

class AccountRepositoryFake(val existingAccounts: UUID*)
    extends AccountRepository {

  private val inMemoryAccounts: mutable.Map[UUID, Account] =
    mutable.Map[UUID, Account]()

  existingAccounts
    .foreach(id => inMemoryAccounts(id) = aNewAccount(id).build())

  override def find(accountId: UUID): Option[Account] =
    inMemoryAccounts.get(accountId)

  override def save(account: Account): Unit =
    inMemoryAccounts.update(account.id, account)
}
