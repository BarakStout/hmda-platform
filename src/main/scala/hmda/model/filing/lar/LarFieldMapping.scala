package hmda.model.filing.lar

object LarFieldMapping {
  def mapping(lar: LoanApplicationRegister): Map[String, Any] = Map(
    "Record Identifier" -> lar.larIdentifier.id,
    "Legal Entity Identifier" -> lar.larIdentifier.LEI,
    "Universal Loan Identifier" -> lar.loan.ULI,
    "Application Date" -> lar.loan.applicationDate,
    "Loan Type" -> lar.loan.loanType.code,
    "Loan Purpose" -> lar.loan.loanPurpose.code,
    "Preapproval" -> lar.action.preapproval.code,
    "Construction Method" -> lar.loan.constructionMethod.code,
    "Occupancy Type" -> lar.loan.occupancy.code,
    "Loan Amount" -> lar.loan.amount,
    "Action Taken" -> lar.action.actionTakenType.code,
    "Action Taken Date" -> lar.action.actionTakenDate,
    "Street Address" -> lar.geography.street,
    "City" -> lar.geography.city,
    "State" -> lar.geography.state,
    "Zip Code" -> lar.geography.zipCode,
    "County" -> lar.geography.county,
    "Census Tract" -> lar.geography.tract,
    "Applicant Ethnicity 1" -> lar.applicant.ethnicity.ethnicity1.code,
    "Applicant Ethnicity 2" -> lar.applicant.ethnicity.ethnicity2.code,
    "Applicant Ethnicity 3" -> lar.applicant.ethnicity.ethnicity3.code,
    "Applicant Ethnicity 4" -> lar.applicant.ethnicity.ethnicity4.code,
    "Applicant Ethnicity 5" -> lar.applicant.ethnicity.ethnicity5.code,
    "Other Hispanic Applicant Ethnicity" -> lar.applicant.ethnicity.otherHispanicOrLatino,
    "Co-Applicant Ethnicity 1" -> lar.coApplicant.ethnicity.ethnicity1.code,
    "Co-Applicant Ethnicity 2" -> lar.coApplicant.ethnicity.ethnicity2.code,
    "Co-Applicant Ethnicity 3" -> lar.coApplicant.ethnicity.ethnicity3.code,
    "Co-Applicant Ethnicity 4" -> lar.coApplicant.ethnicity.ethnicity4.code,
    "Co-Applicant Ethnicity 1" -> lar.coApplicant.ethnicity.ethnicity5.code,
    "Other Hispanic Co-Applicanty Ethnicity" -> lar.coApplicant.ethnicity.otherHispanicOrLatino,
    "Applicant Ethnicity Observed" -> lar.applicant.ethnicity.ethnicityObserved,
    "Co-Applicant Ethnicity Observed" -> lar.coApplicant.ethnicity.ethnicityObserved,
    "Applicant Race 1" -> lar.applicant.race.race1.code,
    "Applicant Race 2" -> lar.applicant.race.race2.code,
    "Applicant Race 3" -> lar.applicant.race.race3.code,
    "Applicant Race 4" -> lar.applicant.race.race4.code,
    "Applicant Race 5" -> lar.applicant.race.race5.code,
    "Other Native Applicant Race" -> lar.applicant.race.otherNativeRace,
    "Other Asian Applicant Race" -> lar.applicant.race.otherAsianRace,
    "Other Pacific Applicant Race" -> lar.applicant.race.otherPacificIslanderRace,
    "Co-Applicant Race 1" -> lar.coApplicant.race.race1.code,
    "Co-Applicant Race 2" -> lar.coApplicant.race.race2.code,
    "Co-Applicant Race 3" -> lar.coApplicant.race.race3.code,
    "Co-Applicant Race 4" -> lar.coApplicant.race.race4.code,
    "Co-Applicant Race 5" -> lar.coApplicant.race.race5.code,
    "Other Native Co-Applicant Race" -> lar.coApplicant.race.otherNativeRace,
    "Other Asian Co-Applicant Race" -> lar.coApplicant.race.otherAsianRace,
    "Other Pacific Co-Applicant Race" -> lar.coApplicant.race.otherPacificIslanderRace,
    "Applicant Race Observed" -> lar.applicant.race.raceObserved.code,
    "Co-Applicant Race Observed" -> lar.coApplicant.race.raceObserved.code,
    "Applicant Sex" -> lar.applicant.sex.sexEnum.code,
    "Co-Applicant Sex" -> lar.coApplicant.sex.sexEnum.code,
    "Applicant Sex Observed" -> lar.applicant.sex.sexObservedEnum.code,
    "Co-Applicant Sex Observed" -> lar.coApplicant.sex.sexObservedEnum.code,
    "Applicant Age" -> lar.applicant.age,
    "Co-Applicant Age" -> lar.coApplicant.age,
    "Income" -> lar.income,
    "Type of Purchaser" -> lar.purchaserType.code,
    "Rate Spread" -> lar.loan.rateSpread,
    "HOEPA Status" -> lar.hoepaStatus.code,
    "Lien Status" -> lar.lienStatus.code,
    "Applicant Credit Score" -> lar.applicant.creditScore,
    "Co-Applicant Credit Score" -> lar.coApplicant.creditScore,
    "Applicant Credit Scoring Model" -> lar.applicant.creditScoreType.code,
    "Other Applicant Credit Scoring Model" -> lar.applicant.otherCreditScoreModel,
    "Co-Applicant Credit Scoring Model" -> lar.coApplicant.creditScoreType.code,
    "Other Co-Applicant Credit Scoring Model" -> lar.coApplicant.otherCreditScoreModel,
    "Reason for Denial 1" -> lar.denial.denialReason1,
    "Reason for Denial 2" -> lar.denial.denialReason2,
    "Reason for Denial 3" -> lar.denial.denialReason3,
    "Reason for Denial 4" -> lar.denial.denialReason4,
    "Other Reason for Denial" -> lar.denial.otherDenialReason,
    "Total Loan Costs" -> lar.loanDisclosure.totalLoanCosts,
    "Total Points and Fees" -> lar.loanDisclosure.totalPointsAndFees,
    "Origination Charges" -> lar.loanDisclosure.originationCharges,
    "Discount Points" -> lar.loanDisclosure.discountPoints,
    "Lender Credits" -> lar.loanDisclosure.lenderCredits,
    "Interest Rate" -> lar.loan.interestRate,
    "Prepayment Penalty Term" -> lar.loan.prepaymentPenaltyTerm,
    "Debt-to-Income Ration" -> lar.loan.debtToIncomeRatio,
    "Combined Loan-to-Value Ration" -> lar.loan.loanToValueRatio,
    "Loan Term" -> lar.loan.loanTerm,
    "Introductory Rate Period" -> lar.loan.introductoryRatePeriod,
    "Balloon Payment" -> lar.nonAmortizingFeatures.balloonPayment.code,
    "Interest-Only Payments" -> lar.nonAmortizingFeatures.interestOnlyPayments.code,
    "Negative Amortization" -> lar.nonAmortizingFeatures.negativeAmortization.code,
    "Other Non-amortizing Features" -> lar.nonAmortizingFeatures.otherNonAmortizingFeatures.code,
    "Property Value" -> lar.property.propertyValue,
    "Manufactured Home Secured Property Type" -> lar.property.manufacturedHomeSecuredProperty,
    "Manufactured Home Land Property Interest" -> lar.property.manufacturedHomeLandPropertyInterest.code,
    "Total Units" -> lar.property.totalUnits,
    "Multifamily Affordable Units" -> lar.property.multiFamilyAffordableUnits,
    "Submission of Application" -> lar.applicationSubmission.code,
    "Initially Payable to Your Institution" -> lar.payableToInstitution.code,
    "Mortgage Loan Originator NMLSR Identifier" -> lar.larIdentifier.NMLSRIdentifier,
    "Automated Underwriting System" -> lar.AUS,
    "Automated Underwriting System Result" -> lar.ausResult,
    "Reverse Mortgage" -> lar.reverseMortgage,
    "Open-End Line of Credit" -> lar.lineOfCredit,
    "Business or Commercial Purpose" -> lar.businessOrCommercialPurpose
  )
}
