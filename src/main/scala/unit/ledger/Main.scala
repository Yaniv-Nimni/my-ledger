package unit.ledger

import unit.ledger.api.{AccountsApi, TransactionsApi}
import unit.ledger.dataaccess.AccountService.AccountServiceEnv
import unit.ledger.dataaccess.{AccountService, TransactionService}
import unit.ledger.dataaccess.Repository.{RepositoryEnv, RepositoryImpl}
import unit.ledger.dataaccess.TransactionService.TransactionServiceEnv
import unit.ledger.domain.AccountDomain.Account
import unit.ledger.domain.PostingDomain.Posting
import unit.ledger.domain.TransactionDomain.Transaction
import zhttp.http.{Http, Request, Response}
import zio.{ExitCode, ULayer, URIO, ZIO}
import zhttp.service.{EventLoopGroup, Server => ZServer}
import zhttp.service.server.ServerChannelFactory
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.random.Random
import zio.system.System

object Main extends zio.App {

  type env = Clock with Console with System with Random with Blocking

  val TransactionServiceLayer: ULayer[TransactionServiceEnv] = (RepositoryImpl.initialize[Transaction] ++ RepositoryImpl.initialize[Posting]) >>> TransactionService.live
  val AccountServiceLayer: ULayer[AccountServiceEnv] = (RepositoryImpl.initialize[Account] ++ TransactionServiceLayer) >>> AccountService.live

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val port = 3334
    (for {
      _ <- ZServer(registerRoutes)
        .withPort(port)
        .start
        .provideSomeLayer[env](EventLoopGroup.auto(0) ++ ServerChannelFactory.auto ++ AccountServiceLayer ++ TransactionServiceLayer)
    } yield ()).exitCode
  }

  private def registerRoutes: Http[AccountServiceEnv with TransactionServiceEnv, Throwable, Request, Response] = {
    AccountsApi.getAccountInfoById ++
    AccountsApi.createNewAccount ++
    AccountsApi.getAllAccounts ++
    TransactionsApi.makeTransaction ++
    TransactionsApi.getAllTransactionsByAccountId
  }
}
