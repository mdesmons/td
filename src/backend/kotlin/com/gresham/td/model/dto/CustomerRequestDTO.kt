package com.gresham.td.model.dto

import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositStatus


class CustomerRequestDTO(		val locationCode: String = "",
								 val name: String = "",
								 val haircutAllowed: Boolean = false,
								 val cacheTDAccount: String = "",
								 val monthlyInterestAllowed : Boolean = false) {
	fun toCustomer() : Customer {
		val customer = Customer(
				name = this.name,
		 locationCode = this.locationCode,
		 haircutAllowed = this.haircutAllowed,
		 cacheTDAccount = this.cacheTDAccount,
		 monthlyInterestAllowed  = this.monthlyInterestAllowed)
		return customer
	}
}