package hmda.model.institution

object TopHolder {
  def empty: TopHolder = TopHolder(None, None)
}

case class TopHolder(
    idRssd: Option[Int],
    name: Option[String]
)
