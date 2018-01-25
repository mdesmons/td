package com.gresham.td.model.dto

import com.gresham.td.model.TermDeposit
import com.gresham.td.model.TermDepositPaymentType


class TermDepositRequestDTO(
	val currency: String = "AUD",
	val sourceAccount: String = "",
	val principal: Double = 0.0,
	val haircut: Double = 0.0,
	val interest: Double = 0.0, // used by the desk to override the proposed rate
	val term:Int = 0, // term in days
	val maturityDate: Long = 0, // in seconds since the epoch. Can be used to specify a maturity date, instead of a term
	val paymentType: TermDepositPaymentType = TermDepositPaymentType.atMaturity
) {
	fun toTermDeposit() : TermDeposit {
		val termDeposit = TermDeposit(currency = this.currency,
				sourceAccount = this.sourceAccount,
				principal = this.principal,
				haircut = this.haircut,
				term = this.term,
				paymentType = this.paymentType)
		return termDeposit
	}
}
