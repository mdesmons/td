package com.gresham.td.api

import com.gresham.td.api.dto.AccountBalanceResponse
import com.gresham.td.model.ClientAccount
import com.gresham.td.model.Customer
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Component
@Profile("dev")
class DevAPIClient : APIClient {
	override fun accountList(currency: String, accountType: String): Iterable<ClientAccount> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun accountBalance(accountHolderRef: String, currency: String, accountType: String): AccountBalanceResponse {
		val response = AccountBalanceResponse()
		response.bankBalance = 10000.0 + Math.random() * 50000.0
		response.bankBalanceDate = Date()
		response.forwardBalance = response.bankBalance
		response.forwardBalanceDate = Date()
		response.ledgerBalance = response.bankBalance
		response.ledgerBalanceDate = Date()

		return response
	}

	override fun createAccount(accountHolderRef: String, description: String, currency: String, type: String): ClientAccount {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun createTransaction() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun closeAccount(accountHolderRef: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun setCustomer(customer: Customer) {
	}
}
