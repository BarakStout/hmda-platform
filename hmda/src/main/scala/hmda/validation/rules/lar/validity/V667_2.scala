package hmda.validation.rules.lar.validity

import hmda.model.filing.lar.LoanApplicationRegister
import hmda.model.filing.lar.enums._
import hmda.validation.dsl.PredicateCommon._
import hmda.validation.dsl.PredicateSyntax._
import hmda.validation.dsl.ValidationResult
import hmda.validation.rules.EditCheck

object V667_2 extends EditCheck[LoanApplicationRegister] {
  override def name: String = "V667-2"

  override def parent: String = "V667"

  override def apply(lar: LoanApplicationRegister): ValidationResult = {
    when(lar.coApplicant.creditScoreType is equalTo(OtherCreditScoreModel)) {
      lar.coApplicant.otherCreditScoreModel not empty
    } and when(lar.coApplicant.otherCreditScoreModel not empty) {
      lar.coApplicant.creditScoreType is equalTo(OtherCreditScoreModel)
    }
  }
}
