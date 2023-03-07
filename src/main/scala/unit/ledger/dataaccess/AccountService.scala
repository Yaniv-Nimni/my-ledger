package unit.ledger.dataaccess

import unit.ledger.contract.AccountDTOs._
import unit.ledger.contract.TransactionDTOs._
import unit.ledger.dataaccess.Repository.{Repository, RepositoryEnv}
import unit.ledger.dataaccess.TransactionService.TransactionServiceEnv
import unit.ledger.domain.AccountDomain._
import zio._

object AccountService {

  type AccountServiceEnv = Has[AccountService.Service]

  val live: ZLayer[RepositoryEnv[Account] with TransactionServiceEnv, Nothing, Has[Service]] =
    ZLayer.fromServices[Repository[Account], TransactionService.Service, AccountService.Service] {
    (accountRepo, transactionService) => Service(accountRepo, transactionService)
  }

  case class Service(accountRepo: Repository[Account], transactionService: TransactionService.Service) {
    def create(createAccountRequest: CreateAccountRequest): UIO[ReturnedAccountObject] = {
      val name    = createAccountRequest.name
      val balance = createAccountRequest.balance
      val account = Account.create(name, "depositAccount", balance)
      for {
        newAccount <- accountRepo.add(account)
      } yield ReturnedAccountObject(newAccount.id, name, balance, newAccount.accountType)
    }

    def getAll: Task[Seq[ReturnedAccountObject]] = for {
      table <- accountRepo.getAll
      accountObjectSeq = table.view.map(account =>
        ReturnedAccountObject(account._2.id,
          account._2.name,
          account._2.balance,
          account._2.accountType)).toSeq
    } yield accountObjectSeq

    def getAccount(id: Int): Task[ReturnedAccountObject] = for {
      account <- accountRepo.get(id)
    } yield ReturnedAccountObject(id, account.name, account.balance, account.accountType)

    def transfer(createTransactionRequest: CreateTransactionRequest): Task[ReturnedTransactionObject] = for {
      from                      <- accountRepo.get(createTransactionRequest.accountId)
      to                        <- accountRepo.get(createTransactionRequest.counterpartyId)
      transaction               <- transactionService.create(from, to, createTransactionRequest.amount, createTransactionRequest.transactionType)
      (updatedFrom, updatedTo)  <- ZIO.fromEither(transactionService.transfer(from, to, createTransactionRequest.amount))
      _                         <- accountRepo.update(updatedFrom.id, updatedFrom)
      _                         <- accountRepo.update(updatedTo.id, updatedTo)
      transactionDTO            <- transactionService.closeTransaction(transaction)
    } yield transactionDTO
  }
}