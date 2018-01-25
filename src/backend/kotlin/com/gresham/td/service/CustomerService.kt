package com.gresham.td.service

import com.gresham.td.model.dto.CustomerDTO
import com.gresham.td.model.dto.CustomerRequestDTO
import com.gresham.td.model.dto.CustomerShortDTO
import com.gresham.td.persistence.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CustomerService() {
	private val logger = LoggerFactory.getLogger(CustomerService::class.java)

	@Autowired
	lateinit var customerRepository: CustomerRepository

	fun customerList(): List<CustomerShortDTO> {
		return customerRepository.findAll().map { CustomerShortDTO(it) }
	}

	fun customer(locationCode: String): CustomerDTO {
		return CustomerDTO(customerRepository.findByLocationCode(locationCode))
	}

	/* Return the list of ladders with their subscriptions */
	fun addCustomer(request: CustomerRequestDTO): CustomerDTO {
		val customer = request.toCustomer()
		customerRepository.save(customer)
		return CustomerDTO(customer)
	}
}
