package hmda.model.institution

import org.scalacheck.Gen
import hmda.model.filing.FilingGenerators._

object InstitutionGenerators {

  implicit def institutionGen: Gen[Institution] = {
    for {
      activityYear <- activityYearGen
      lei <- Gen.option(leiGen)
      agency <- agencyGen
      institutionType <- institutionTypeGen
      id2017 <- Gen.alphaStr
      taxId <- Gen.alphaStr
      rssd <- Gen.alphaStr
      email <- Gen.option(emailGen)
      respondent <- institutionRespondentGen
      parent <- institutionParentGen
      assets <- Gen.option(Gen.choose(Int.MinValue, Int.MaxValue))
      otherLenderCode <- Gen.option(Gen.choose(Int.MinValue, Int.MaxValue))
      topHolder <- topHolderGen
      hmdaFiler <- Gen.oneOf(true, false)
    } yield {
      Institution(
        activityYear: Int,
        lei,
        Some(agency),
        Some(institutionType),
        if (id2017 == "") None else Some(id2017),
        if (taxId == "") None else Some(taxId),
        if (rssd == "") None else Some(rssd),
        email,
        respondent,
        parent,
        assets,
        otherLenderCode: Option[Int],
        topHolder: TopHolder,
        hmdaFiler: Boolean
      )
    }
  }

  implicit def institutionTypeCodeGen: Gen[Int] = {
    Gen.oneOf(InstitutionType.values.filter(x => x != -1))
  }

  implicit def institutionTypeGen: Gen[InstitutionType] = {
    for {
      institutionTypeCode <- institutionTypeCodeGen
      institutionType = InstitutionType.valueOf(institutionTypeCode)
    } yield institutionType
  }

  implicit def institutionRespondentGen: Gen[Respondent] = {
    for {
      name <- Gen.option(Gen.alphaStr.suchThat(!_.isEmpty))
      state <- Gen.option(stateGen)
      city <- Gen.option(Gen.alphaStr.suchThat(!_.isEmpty))
    } yield Respondent(name, state, city)
  }

  implicit def institutionParentGen: Gen[Parent] = {
    for {
      idRssd <- Gen.option(Gen.choose(Int.MinValue, Int.MaxValue))
      name <- Gen.option(Gen.alphaStr.suchThat(!_.isEmpty))
    } yield Parent(idRssd, name)
  }

  implicit def topHolderGen: Gen[TopHolder] = {
    for {
      idRssd <- Gen.option(Gen.choose(Int.MinValue, Int.MaxValue))
      name <- Gen.option(Gen.alphaStr.suchThat(!_.isEmpty))
    } yield TopHolder(idRssd, name)
  }

}
