package unit.ledger.api

import unit.ledger.contract.TransactionDTOs.CreateTransactionRequest
import unit.ledger.dataaccess.AccountService.AccountServiceEnv
import unit.ledger.dataaccess.TransactionService.TransactionServiceEnv
import unit.ledger.dataaccess.{AccountService, TransactionService}
import zhttp.endpoint._
import zhttp.http.Method._
import zhttp.http._
import zio.ZIO
import zio.json.{DecoderOps, EncoderOps}

object TransactionsApi {

  val getAllTransactionsByAccountId: HttpApp[TransactionServiceEnv, Throwable] = GET / "transactions" / *[Int] to { accountId =>
      for {
      transactionService    <- ZIO.service[TransactionService.Service]
      listOfTransactionDTOs <- transactionService.getAllTransactionsOfAccount(accountId.params)
    } yield Response.json(listOfTransactionDTOs.toJson)
  }


  val makeTransaction: HttpApp[AccountServiceEnv, Throwable] = POST / "transactions" to { p =>
    for {
      payload         <- p.bodyAsString
      accountService  <- ZIO.service[AccountService.Service]
      transactionDTO  <- payload.fromJson[CreateTransactionRequest] match {
        case Right(transactionRequest)  => accountService.transfer(transactionRequest)
        case Left(exception)            => ZIO.fail(new Exception(exception))
      }
    } yield Response.json(transactionDTO.toJson)
  }
}
