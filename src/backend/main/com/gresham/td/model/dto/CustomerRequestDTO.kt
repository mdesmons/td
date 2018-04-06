package com.gresham.td.model.dto

import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositStatus


class CustomerRequestDTO(		val locationCode: String = "",
								 val name: String = "",
								 val haircutAllowed: Boolean = false,
								 val cacheTDAccount: String = "",
								 val certificate: String = "",
								 val keystorePass: String = "",
								 val keyAlias: String = ""
								 ) {
	fun toCustomer() : Customer {
		val customer = Customer(
		name = this.name,
		 locationCode = this.locationCode,
		 haircutAllowed = this.haircutAllowed,
		 cacheTDAccount = this.cacheTDAccount,
		certificate = this.certificate,
		keyAlias = this.keyAlias,
		keystorePass = this.keystorePass
		)
		return customer
	}
}
