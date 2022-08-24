package banking.domain

trait StatementFormatter {
  def format(transactions: List[Transaction]): String
}
