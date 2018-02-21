package com.gresham.td.controller

import com.gresham.td.model.dto.*
import com.gresham.td.service.CustomerService
import com.gresham.td.service.InterestService
import com.gresham.td.service.TermDepositService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import java.security.Principal
import java.util.*

@RestController
@RequestMapping("/api/v1/customer")
class CustomerControllerV1 {
	private val logger = LoggerFactory.getLogger(CustomerControllerV1::class.java)

	@Autowired
	lateinit var termDepositService: TermDepositService

	@Autowired
	lateinit var rateService: InterestService

	@Autowired
	lateinit var customerService: CustomerService

	/* Get the list of onboarded customers */
	@GetMapping("/")
	fun customers(principal: Principal?): Map<String, List<CustomerShortDTO>> {
		if (principal != null) {
			return hashMapOf("customers" to customerService.customerList(principal.name))
		}
		throw IllegalArgumentException("Unknown user")
	}

	/* onboard a new customer */
	@PostMapping("/")
	fun addCustomer(principal: Principal?, @RequestBody customer: CustomerRequestDTO): Map<String, List<CustomerDTO>> {

		if (principal != null) {
			return hashMapOf("customers" to listOf<CustomerDTO>(customerService.addCustomer(principal.name, customer)))
		}

		throw IllegalArgumentException("Unknown user")
	}

	/* Get a customer details */
	@GetMapping("/{location_code}/")
	fun customer(principal: Principal?, @PathVariable location_code: String): Map<String, List<CustomerDTO>> {
		if (principal != null) {
			return hashMapOf("customers" to listOf(customerService.customer(principal.name, location_code)))
		}
		throw IllegalArgumentException("Unknown user")
	}

	// create a TD for a customer
	@PostMapping("/{location_code}/td/")
	fun addTermDeposit(principal: Principal?, @PathVariable location_code: String, @RequestBody request: List<TermDepositRequestDTO>) : Map<String, List<TermDepositDTO>> {
		if (principal != null) {
			val response = request.map { termDepositService.addTermDeposit(principal.name, location_code, it) }
			return hashMapOf("termDeposits" to response)
		}
		throw IllegalArgumentException("Unknown user")
	}

	// get the interest rate for a TD
	@PutMapping("/{location_code}/rate/")
	fun rate(principal: Principal?, @PathVariable location_code: String, @RequestBody request: RateRequestDTO): Map<String, List<RateDTO>> {
		if (principal != null) {
			if (request.term != 0) {
				return hashMapOf("interestRate" to listOf(rateService.getRate(
						principal.name,
						location_code,
						request.term,
						request.principal,
						request.paymentType
				)))
			} else {
				val maturityDate = Date()
				maturityDate.time = request.maturity
				return hashMapOf("interestRate" to listOf(rateService.getRate(
						principal.name,
						location_code,
						Date(),
						maturityDate,
						request.principal,
						request.paymentType
				)))
			}
		}
		throw IllegalArgumentException("Unknown user")
	}

	protected fun formatError(error: String?) = hashMapOf("error" to ErrorDTO(error?:"Unknown error"))

	@ExceptionHandler(IllegalArgumentException::class)
	protected fun handleIllegalArgument(ex: IllegalArgumentException, request: WebRequest) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(formatError(ex.message))
}
