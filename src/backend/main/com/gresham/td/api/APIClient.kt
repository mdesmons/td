package com.gresham.td.api

import com.gresham.td.api.dto.AccountBalanceResponse
import com.gresham.td.model.ClientAccount
import com.gresham.td.model.Customer
import java.time.temporal.TemporalAmount

interface APIClient {
	/* Actually reading the account list from the DB, which is faster and there's no API currently supporting that anyway */
	fun accountList(currency: String = "AUD", accountType: String = "CLI"): Iterable<ClientAccount>

	fun accountBalance(accountHolderRef: String, currency: String = "AUD", accountType: String = "CLI"): AccountBalanceResponse
	//TODO
	// Creates a term deposit account
	fun createAccount(accountHolderRef: String, description: String, currency: String = "AUD", type: String = "CTD" ): ClientAccount

	//TODO
	fun createTransaction()

	//TODO
	fun closeAccount(accountHolderRef: String)

	fun setCustomer(customer: Customer)
}
