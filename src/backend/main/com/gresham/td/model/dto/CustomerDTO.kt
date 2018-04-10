package com.gresham.td.model.dto

import com.gresham.td.model.Customer
import com.gresham.td.model.QuoteStatus
import com.gresham.td.model.TermDepositStatus


class CustomerDTO(customer: Customer) : CustomerShortDTO(customer) {
	val termDeposits = customer.termDeposits.filter { it.status != TermDepositStatus.Closed }.map { TermDepositDTO(it) }
	val clientAccounts = customer.clientAccounts.map { ClientAccountDTO(it) }
	val certificate = customer.certificate
	val keyAlias = customer.keyAlias
	val quotes = customer.quotes.filter { it.status != QuoteStatus.Closed }.map { QuoteDTO(it) }
}
