package com.gresham.td.model.dto

import com.gresham.td.model.ClientAccount
import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositStatus
import javax.persistence.Column


class ClientAccountDTO(clientAccount: ClientAccount) {
	var id = clientAccount.id
	var name = clientAccount.name
	var type = clientAccount.type
	var currency = clientAccount.currency
}
