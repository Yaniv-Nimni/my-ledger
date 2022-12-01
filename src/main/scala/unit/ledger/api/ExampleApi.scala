package unit.ledger.api

import zhttp.http._
import zhttp.endpoint._
import zhttp.http.Method._
import zio.{ZEnv, ZIO}

object ExampleApi {
  // get request example
  val simpleGet = GET / "example" to { p =>
    for {
      _ <- ZIO.debug(p.url.queryParams)
    } yield Response.text("test")
  }

  // get request with resource id: localhost/example/5
  val getWithResourceId = GET / "example" / *[Int] / *[String] to { id =>
    for {
      _ <- ZIO.debug(id)
    } yield Response.text(s"test")
  }


  // post request example...
  val create = POST / "example" to { p =>
    for {
      payload <- p.bodyAsString
      _ <- ZIO.debug(payload)
    } yield Response.text(payload)
  }
}
