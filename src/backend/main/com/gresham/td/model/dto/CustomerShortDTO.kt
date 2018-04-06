package com.gresham.td.model.dto

import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositStatus


open class CustomerShortDTO(customer: Customer) {
	val name = customer.name
	val locationCode = customer.locationCode
	val naturalAccount = customer.naturalAccount
	val naturalAccountCurrency = customer.naturalAccountCurrency
	val haircutAllowed = customer.haircutAllowed
	val naturalTDAccount = customer.naturalTDAccount
	val interestTDAccount = customer.interestTDAccount
	val controlTDAccount = customer.controlTDAccount
	val haircutAccount = customer.haircutAccount
	val cacheTDAccount = customer.cacheTDAccount
}
