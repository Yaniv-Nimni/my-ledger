package unit.ledger.api

import unit.ledger.contract.AccountDTOs.CreateAccountRequest
import unit.ledger.dataaccess.AccountService
import unit.ledger.dataaccess.AccountService.AccountServiceEnv
import zio.json._
import zhttp.endpoint._
import zhttp.http.Method._
import zhttp.http._
import zio._

object AccountsApi {

  // get request with resource id: localhost/accounts/:id
  val getAccountInfoById: HttpApp[AccountServiceEnv, Throwable] = GET / "accounts" / *[Int] to { id =>
    for {
      accountService <- ZIO.service[AccountService.Service]
      accountDTO <- accountService.getAccount(id.params)
    } yield Response.json(accountDTO.toJson)
  }


  // post request example...
  val createNewAccount: HttpApp[AccountServiceEnv, Throwable] = POST / "accounts" to { p =>
    for {
      payload         <- p.bodyAsString
      accountService  <- ZIO.service[AccountService.Service]
      accountDTO      <- payload.fromJson[CreateAccountRequest] match {
        case Right(accountRequest)  => accountService.create(accountRequest)
        case Left(exception)        => ZIO.fail(new Exception(exception))
      }
    } yield Response.json(accountDTO.toJson)
  }

  val getAllAccounts: HttpApp[AccountServiceEnv, Throwable] = GET / "accounts" to { p =>
    for {
      accountService <- ZIO.service[AccountService.Service]
      accountDTOs <- accountService.getAll
    } yield Response.json(accountDTOs.toJson)
  }
}
