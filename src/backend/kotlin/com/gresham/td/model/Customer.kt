package com.gresham.td.model

import javax.persistence.*

@Entity
class Customer (
		@Id
		var locationCode: String = "",

		var name: String = "",
		var naturalAccount: String = "",
		var naturalAccountCurrency: String = "AUD",
		var haircutAllowed: Boolean = false,
		var naturalTDAccount: String = "",
		var interestTDAccount: String = "",
		var controlTDAccount: String = "",
		var haircutAccount: String = "",
		var cacheTDAccount: String = "",
		var monthlyInterestAllowed : Boolean = false,

		// a customer has several TDs
		@OneToMany(mappedBy = "customer", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
		var termDeposits: MutableSet<TermDeposit> = mutableSetOf<TermDeposit>()
)

