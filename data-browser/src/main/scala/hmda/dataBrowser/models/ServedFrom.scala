package hmda.dataBrowser.models

import io.circe.{Encoder, Json}
// $COVERAGE-OFF$
sealed trait ServedFrom extends Product with Serializable { self =>
  import ServedFrom._
  def combine(other: ServedFrom): ServedFrom = (self, other) match {
    case (Database, _) => Database
    case (_, Database) => Database
    case _             => Cache
  }
}

object ServedFrom {
  case object Database extends ServedFrom
  case object Cache    extends ServedFrom

  implicit val servedFromEncoder: Encoder[ServedFrom] = Encoder[String].contramap {
    case Database => "db"
    case Cache    => "cache"
  }
}
// $COVERAGE-ON$