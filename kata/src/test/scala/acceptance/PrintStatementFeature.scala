package acceptance

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers

class PrintStatementFeature extends AnyFlatSpec with Matchers {
  behavior of "Account API"

  it should "print statement containing all the transactions" in {
    true mustBe false
  }
}
