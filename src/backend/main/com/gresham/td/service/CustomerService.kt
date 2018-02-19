package com.gresham.td.service

import com.gresham.td.api.APIClient
import com.gresham.td.api.VBTAPIClient
import com.gresham.td.api.CustomerCryptoManager
import com.gresham.td.model.dto.ClientAccountDTO
import com.gresham.td.model.dto.CustomerDTO
import com.gresham.td.model.dto.CustomerRequestDTO
import com.gresham.td.model.dto.CustomerShortDTO
import com.gresham.td.persistence.ClientAccountRepository
import com.gresham.td.persistence.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import java.io.File
import java.security.Principal

@Service
class CustomerService() {
	private val logger = LoggerFactory.getLogger(CustomerService::class.java)

	@Autowired
	lateinit var apiClient: APIClient

	@Autowired
	lateinit var customerRepository: CustomerRepository

	@Autowired
	lateinit var clientAccountRepository: ClientAccountRepository

	fun customerList(principal: Principal?): List<CustomerShortDTO> {
		return customerRepository.findAll().map { CustomerShortDTO(it) }
	}

	fun customer(locationCode: String): CustomerDTO {
		val customer = customerRepository.findByLocationCode(locationCode)
		val accountList = clientAccountRepository.clientAccounts(locationCode, "CLI")
		customer.clientAccounts.addAll(accountList)
		return CustomerDTO(customer)
	}

	/* Return the list of ladders with their subscriptions */
	fun addCustomer(request: CustomerRequestDTO): CustomerDTO {
		val customer = request.toCustomer()
		customerRepository.save(customer)
		return CustomerDTO(customer)
	}

	fun clientAccountDetails(principal: Principal?, @PathVariable id: String) : ClientAccountDTO {
		val account = clientAccountRepository.findById(id)
		if (account != null) {
			// get the account balance

			// get the account location
			val locationCode = account.id.substring(0, 6)
			val customer = customerRepository.findByLocationCode(locationCode)
			apiClient.setCustomer(customer)
			account.setBalances(apiClient.accountBalance(account.id, account.currency, account.type))
			return ClientAccountDTO(account)
		}
		throw IllegalArgumentException("Invalid parameters: account not found")
	}
}
