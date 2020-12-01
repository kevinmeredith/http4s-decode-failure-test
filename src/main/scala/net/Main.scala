package net

import java.net.InetAddress

import cats.effect.{Effect, IO}
import cats.implicits._
import fs2.StreamApp.ExitCode
import fs2._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object Main extends StreamApp[IO] {

  private implicit val dsl: Http4sDsl[IO] = Http4sDsl[IO]

  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] = {

    val app: HttpService[IO] = App.route[IO]

    val s: Stream[IO, Unit] = for {
      localHost <- Stream.eval(IO(InetAddress.getLoopbackAddress.getHostAddress))
      _ <-  BlazeBuilder[IO].bindHttp(8080, localHost).mountService(app).serve(Effect[IO], ExecutionContext.global)
    } yield ()

    s.as(ExitCode.Success)
  }
}
