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

		var margin: Double = 0.0,   // customer TDs rate will be BBSW minus margin (minus payment type penalty)
		// stuff for VBT API comms management
		var certificate: String = "",
		var keystorePass: String = "",
		var keyAlias: String = "",

		@Transient
		var clientAccounts : MutableList<ClientAccount> = mutableListOf(),

		// a customer has several TDs
		@OneToMany(mappedBy = "customer", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
		var termDeposits: MutableSet<TermDeposit> = mutableSetOf<TermDeposit>(),

		// a customer has several Quotes
		@OneToMany(mappedBy = "customer", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
		var quotes: MutableSet<Quote> = mutableSetOf<Quote>()
)

