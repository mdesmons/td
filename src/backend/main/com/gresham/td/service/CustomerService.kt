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

	/**
	 * Gets the list of location codes a user has access to
	 *
	 * @param username the user login
	 * @return a list of static customer details
	 */
	fun customerList(username: String): List<CustomerShortDTO> {
		/* Check the user exists */
		val applicationUser = applicationUserRepository.findByUsername(username) ?: throw IllegalArgumentException("Permission error")

		/* If the user has access to all location codes, return everything */
		if (applicationUser.locationCodes == "*") {
			return customerRepository.findAll().map { CustomerShortDTO(it) }
		} else {
			val locationCodes = applicationUser.locationCodes.split(",")
			return customerRepository.findAll().filter{locationCodes.contains(it.locationCode)}.map { CustomerShortDTO(it) }
		}
	}

	/**
	 * Get the details of a customer
	 * @param username the user login
	 * @param locationCode the customer location code
	 * @return the customer details
	 */
	fun customer(username: String, locationCode: String): CustomerDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		/* The client account list for this customer is stored in VBT and not locally, so retrieve it via API */
		val accountList = clientAccountRepository.clientAccounts(locationCode, "CLI")
		customer.clientAccounts.addAll(accountList)

		return CustomerDTO(customer)
	}

	/**
	 * Onbard a new customer. This creates the customer in the application database
	 * @param username the user login
	 * @param request the customer details
	 * @return the customer details
	 */
	/* Onboard a new customer */
	fun addCustomer(username: String, request: CustomerRequestDTO): CustomerDTO {
		val customer = request.toCustomer()
		userDetailsService.canAccessLocation(username, customer.locationCode) || throw IllegalArgumentException("Permission error")

		// verify the customer doesn't exist yet
		val existingCustomer = customerRepository.findByLocationCode(customer.locationCode)
		if (existingCustomer != null) throw IllegalArgumentException("Customer already exists")

		// TODO set certificate path, alias, keystore password
		// TODO find support account numbers from database
		customer.naturalTDAccount = "123456"
		customer.interestTDAccount = "234567"
		customer.controlTDAccount = "345678"
		customer.haircutAccount = "456789"
		customer.cacheTDAccount = "567890"
		customer.naturalAccount = "678901"

		customerRepository.save(customer)
		return CustomerDTO(customer)
	}

	/**
	 * Get the details (name, currency, balances...) of a client account
	 * @param username user login
	 * @param id client account holder reference
	 * @return the client account details object
	 */
	fun clientAccountDetails(username: String, id: String) : ClientAccountDTO {
		// get the account location
		val locationCode = id.substring(0, 6)
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")

		/* Verify that an acocunt with this Id exists in the integration database (CLILST table) */
		val account = clientAccountRepository.findById(id) ?: throw IllegalArgumentException("Account not found")

		/* Get the customer object  to retrieve their API key */
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		/* get the account balance by invoking the VBT API */
		account.setBalances(apiClient.setCustomer(customer).accountBalance(account.id, account.currency, account.type))

		return ClientAccountDTO(account)
	}
}
