package unit.ledger.dataaccess

import unit.ledger.contract.TransactionDTOs.ReturnedTransactionObject
import unit.ledger.dataaccess.Repository.{Repository, RepositoryEnv}
import unit.ledger.domain.AccountDomain.Account
import unit.ledger.domain.PostingDomain.Posting
import unit.ledger.domain.TransactionDomain.Transaction
import zio._

object TransactionService {

  type TransactionServiceEnv = Has[TransactionService.Service]

  val live: ZLayer[RepositoryEnv[Transaction] with RepositoryEnv[Posting], Nothing, Has[Service]] =
    ZLayer.fromServices[Repository[Transaction], Repository[Posting], TransactionService.Service] {
    (transactionRepo, postingRepo) => Service(transactionRepo, postingRepo)
  }

  case class Service(transactionRepo: Repository[Transaction], postingRepo: Repository[Posting]) {
    def create(from: Account, to: Account, amount: Int, transactionType: String): Task[Transaction] = {

      transactionType match {
        //deposit makes transaction with Unit cash account, therefore money moved from it TO the requesting account.
        case "deposit"                  => ZIO(Transaction(0, transactionType, amount, to.id, from.id))
        case "withdraw" | "bookPayment" =>
          if (from.balance < amount)  ZIO.fail(new Exception("not enough funds for transaction"))
          else ZIO(Transaction(0, transactionType, amount, from.id, to.id))
        case _                          => ZIO.fail(new Exception("Invalid Transaction Error!"))
      }
    }

    def transfer(from: Account, to: Account, amount: Int): Either[Throwable, (Account, Account)] = {
      if (from.balance < amount) Left(new Exception("not enough funds for transaction"))
      else {
        val updatedFrom = from.copy(balance = from.balance - amount)
        val updatedTo   = to.copy(balance = to.balance + amount)
        Right((updatedFrom, updatedTo))
      }
    }

    def closeTransaction(transaction: Transaction): ZIO[Any, Nothing, ReturnedTransactionObject] = {
      val postings = transaction.commit
      for {
        transaction <- transactionRepo.add(transaction)
        _           <- postingRepo.add(postings._1)
        _           <- postingRepo.add(postings._2)
      }
      yield ReturnedTransactionObject(
        transaction.id,
        transaction.txType,
        transaction.amount,
        transaction.accId,
        transaction.counterpartyId)
    }

    def getAllTransactionsOfAccount(id: Int): UIO[List[ReturnedTransactionObject]] = for {
      transactions        <- transactionRepo.getWhere(transaction => transaction.accId == id)
      listOfTransactions  = transactions.view.map(transaction =>
        ReturnedTransactionObject(transaction._2.id,
                                  transaction._2.txType,
                                  transaction._2.amount,
                                  transaction._2.accId,
                                  transaction._2.counterpartyId)).toList
    } yield listOfTransactions
  }
}
