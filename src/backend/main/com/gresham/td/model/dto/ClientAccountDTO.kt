package com.gresham.td.model.dto

import com.gresham.td.model.ClientAccount
import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositStatus
import java.util.*
import javax.persistence.Column


class ClientAccountDTO(clientAccount: ClientAccount) {
	var id = clientAccount.id
	var name = clientAccount.name
	var type = clientAccount.type
	var currency = clientAccount.currency
	val bankBalance= clientAccount.bankBalance
	val bankBalanceDate= clientAccount.bankBalanceDate
	val forwardBalance= clientAccount.forwardBalance
	val forwardBalanceDate= clientAccount.forwardBalanceDate
	val ledgerBalance= clientAccount.ledgerBalance
	val ledgerBalanceDate= clientAccount.ledgerBalanceDate
}
