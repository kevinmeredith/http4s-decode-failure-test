package net

import cats.implicits._
import cats.effect.{IO, Sync}
import io.circe._
import io.circe.generic.semiauto.deriveDecoder
import fs2.{Stream, io, text}
import org.http4s._
import org.http4s.circe.jsonOf

object App {

  def route[F[_] : Sync]: HttpService[F] = HttpService.apply[F] {
    case req : Request[F] =>
      req
        .as[Foo]
        .as( Response[F](status = Status.Ok) )
        .onError {
          case d: DecodeFailure => Sync[F].delay(println(">>> " + d.getMessage()))
        }
  }

  final case class Foo(a: String)
  object Foo {
    implicit val d: Decoder[Foo] = deriveDecoder[Foo]
    implicit def entityDecoder[F[_] : Sync]: EntityDecoder[F, Foo] = jsonOf[F, Foo]
  }

}
