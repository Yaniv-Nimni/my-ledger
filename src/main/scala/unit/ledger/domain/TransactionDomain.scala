package unit.ledger.domain

import unit.ledger.domain.AccountDomain.{Entity, IdUpdater}
import unit.ledger.domain.PostingDomain._

object TransactionDomain {

  case class Transaction private (id: Int,
                         txType: String,
                         amount: Int,
                         accId: Int,
                         counterpartyId: Int = 1) extends Entity {


    def commit: (Posting,Posting) = txType match {
      case "deposit"
      =>   (Posting.create(id, this.accId, Direction.Credit, this.amount),
            Posting.create(id, this.counterpartyId, Direction.Debit, this.amount))

      case "withdraw" | "bookPayment"
      =>   (Posting.create(id, this.accId, Direction.Debit, this.amount),
            Posting.create(id, this.counterpartyId, Direction.Credit, this.amount))
    }
  }
  object Transaction {
    def create(txType: String,
               amount: Int,
               accId: Int,
               counterpartyId: Int = 1): Transaction = Transaction(-1, txType, amount, accId, counterpartyId)

    implicit val idUpdater: IdUpdater[Transaction] = (x: Transaction, id: Int) => x.copy(id = id)
  }
}