package unit.ledger

import sttp.client3.httpclient.zio.HttpClientZioBackend
import unit.ledger.api.ExampleApi
import zio.{ExitCode, URIO}
import zhttp.endpoint._
import zhttp.service.{EventLoopGroup, Server => ZServer}
import zhttp.http.Method.GET
import zhttp.http.Response
import zhttp.service.server.ServerChannelFactory
import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.logging.Logging
import zio.random.Random
import zio.system.System

object Main extends zio.App {

  type env = Clock with Console with System with Random with Blocking

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = {
    val port = 3334
    (for {
      _ <- ZServer(registerRoutes)
        .withPort(port)
        .start
        .provideSomeLayer[env](EventLoopGroup.auto(0) ++ ServerChannelFactory.auto)
    } yield ()).exitCode
  }

  private def registerRoutes = {
    ExampleApi.simpleGet ++
      ExampleApi.create ++
      ExampleApi.getWithResourceId
  }
}
