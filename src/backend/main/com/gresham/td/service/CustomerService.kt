package com.gresham.td.service

import com.gresham.td.api.APIClient
import com.gresham.td.api.VBTAPIClient
import com.gresham.td.api.CustomerCryptoManager
import com.gresham.td.model.ApplicationUser
import com.gresham.td.model.dto.ClientAccountDTO
import com.gresham.td.model.dto.CustomerDTO
import com.gresham.td.model.dto.CustomerRequestDTO
import com.gresham.td.model.dto.CustomerShortDTO
import com.gresham.td.persistence.ApplicationUserRepository
import com.gresham.td.persistence.ClientAccountRepository
import com.gresham.td.persistence.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
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

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	@Autowired
	lateinit var applicationUserRepository: ApplicationUserRepository

	/* Return the list of customers that the user has access to */
	fun customerList(username: String): List<CustomerShortDTO> {
		val applicationUser = applicationUserRepository.findByUsername(username) ?: throw IllegalArgumentException("Permission error")

		if (applicationUser.locationCodes == "*") {
			return customerRepository.findAll().map { CustomerShortDTO(it) }
		} else {
			val locationCodes = applicationUser.locationCodes.split(",")
			return customerRepository.findAll().filter{locationCodes.contains(it.locationCode)}.map { CustomerShortDTO(it) }
		}
	}

	/* Get the details of a customer */
	fun customer(username: String, locationCode: String): CustomerDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")
		val accountList = clientAccountRepository.clientAccounts(locationCode, "CLI")
		customer.clientAccounts.addAll(accountList)
		return CustomerDTO(customer)
	}

	/* Onboard a new customer */
	fun addCustomer(username: String, request: CustomerRequestDTO): CustomerDTO {
		val customer = request.toCustomer()
		userDetailsService.canAccessLocation(username, customer.locationCode) || throw IllegalArgumentException("Permission error")

		// verify the customer doesn't exist yet
		val existingCustomer = customerRepository.findByLocationCode(customer.locationCode)
		if (existingCustomer != null) throw IllegalArgumentException("Customer already exists")

		customerRepository.save(customer)
		return CustomerDTO(customer)
	}

	fun clientAccountDetails(username: String, @PathVariable id: String) : ClientAccountDTO {
		// get the account location
		val locationCode = id.substring(0, 6)
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val account = clientAccountRepository.findById(id) ?: throw IllegalArgumentException("Account not found")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		// get the account balance
		account.setBalances(apiClient.setCustomer(customer).accountBalance(account.id, account.currency, account.type))

		return ClientAccountDTO(account)
	}
}
