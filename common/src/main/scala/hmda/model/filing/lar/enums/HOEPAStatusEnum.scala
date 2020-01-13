package hmda.model.filing.lar.enums

sealed trait HOEPAStatusEnum extends LarEnum

object HOEPAStatusEnum extends LarCodeEnum[HOEPAStatusEnum] {
  override val values = List(1, 2, 3)

  override def valueOf(code: Int): HOEPAStatusEnum =
    code match {
      case 1 => HighCostMortgage
      case 2 => NotHighCostMortgage
      case 3 => HOEPStatusANotApplicable
      case other => new InvalidHoepaStatusCode(other)
    }
}

case object HighCostMortgage extends HOEPAStatusEnum {
  override val code: Int           = 1
  override val description: String = "High-cost mortgage"
}

case object NotHighCostMortgage extends HOEPAStatusEnum {
  override val code: Int           = 2
  override val description: String = "Not a high-cost mortgage"
}

case object HOEPStatusANotApplicable extends HOEPAStatusEnum {
  override val code: Int           = 3
  override val description: String = "Not applicable"
}

class InvalidHoepaStatusCode(value: Int = -1) extends HOEPAStatusEnum {
  override def code: Int           = value
  override def description: String = "Invalid Code"
}
