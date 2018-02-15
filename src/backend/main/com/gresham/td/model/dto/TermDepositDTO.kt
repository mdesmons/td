package com.gresham.td.model.dto

import com.gresham.td.model.TermDeposit

class TermDepositDTO(termDeposit: TermDeposit) {
	val id = termDeposit.id
	val account = termDeposit.account
	val currency = termDeposit.currency
	val sourceAccount = termDeposit.sourceAccount
	val principal = termDeposit.principal
	val interest = termDeposit.interest
	val haircut = termDeposit.haircut
	val term = termDeposit.term
	val openingDate = termDeposit.openingDate
	val valueDate = termDeposit.valueDate
	val maturityDate = termDeposit.maturityDate
	val closingDate = termDeposit.closingDate
	val paymentType = termDeposit.paymentType
	val status = termDeposit.status
	val reasonForClose = termDeposit.reasonForClose
	val transfers = termDeposit.transfers.map { TransferDTO(it) }
}
