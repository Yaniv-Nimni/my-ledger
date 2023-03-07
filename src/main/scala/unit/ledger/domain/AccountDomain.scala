package unit.ledger.domain

object AccountDomain {

  trait IdUpdater[A] {
    def updateId(x: A, id: Int): A
  }

  trait Entity {
    val id: Int
  }

  case class Account private (id: Int, name: String, balance: Int, accountType: String) extends Entity {
  }
  object Account {
    def create(name: String, accountType: String, balance: Int = 0): Account = Account(-1, name, balance, accountType)

    implicit val idUpdater: IdUpdater[Account] = (x: Account, id: Int) => x.copy(id = id)
  }
}