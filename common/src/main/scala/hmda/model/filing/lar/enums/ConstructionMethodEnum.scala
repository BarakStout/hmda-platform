package hmda.model.filing.lar.enums

sealed trait ConstructionMethodEnum extends LarEnum

object ConstructionMethodEnum extends LarCodeEnum[ConstructionMethodEnum] {
  override val values = List(1, 2)

  override def valueOf(code: Int): ConstructionMethodEnum =
    code match {
      case 1 => SiteBuilt
      case 2 => ManufacturedHome
      case other => new InvalidConstructionMethodCode(other)
    }
}

case object SiteBuilt extends ConstructionMethodEnum {
  override val code: Int           = 1
  override val description: String = "Site-built"
}

case object ManufacturedHome extends ConstructionMethodEnum {
  override val code: Int           = 2
  override val description: String = "Manufactured Home"
}

class InvalidConstructionMethodCode(value: Int = -1) extends ConstructionMethodEnum {
  override def code: Int           = value
  override def description: String = "Invalid Code"
}
