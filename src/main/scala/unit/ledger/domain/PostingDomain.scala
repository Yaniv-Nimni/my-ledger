package unit.ledger.domain

import unit.ledger.domain.AccountDomain.{Entity,IdUpdater}

object PostingDomain {

  sealed trait Direction
  object Direction {
    case object Credit extends Direction
    case object Debit extends Direction
  }

  case class Posting private(id: Int, txId: Int, accId: Int, direction: Direction, amount: Int) extends Entity
  object Posting {
    def create(txId: Int, accId: Int, direction: Direction, amount: Int): Posting = Posting(-1, txId, accId, direction, amount)

    implicit val idUpdater: IdUpdater[Posting] = (x: Posting, id: Int) => x.copy(id = id)
  }

}
