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
class CustomerControllerV1() {
	private val logger = LoggerFactory.getLogger(CustomerControllerV1::class.java)

	@Autowired
	lateinit var termDepositService: TermDepositService

	@Autowired
	lateinit var rateService: InterestService

	@Autowired
	lateinit var customerService: CustomerService

	/* Get the list of onboarded customers */
	@GetMapping("/")
	fun customers(): Map<String, List<CustomerShortDTO>>
		= hashMapOf("customers" to customerService.customerList())

	/* onboard a new customer */
	@PostMapping("/")
	fun addCustomer(@RequestBody customer: CustomerRequestDTO): Map<String, List<CustomerDTO>>
			= hashMapOf("customers" to listOf<CustomerDTO>(customerService.addCustomer(customer)))

	/* Get a customer details */
	@GetMapping("/{location_code}/")
	fun customer(@PathVariable location_code: String): Map<String, List<CustomerDTO>>
			= hashMapOf("customers" to listOf(customerService.customer(location_code)))

	// create a TD for a customer
	@PostMapping("/{location_code}/td/")
	fun addTermDeposit(principal: Principal?, @PathVariable location_code: String, @RequestBody request: TermDepositRequestDTO) : Map<String, List<TermDepositDTO>>
			= hashMapOf("termDeposits" to listOf<TermDepositDTO>(termDepositService.addTermDeposit(principal, location_code, request)))

	@PutMapping("/{location_code}/rate/")
	fun rate(@PathVariable location_code: String, @RequestBody request: RateRequestDTO): Map<String, List<RateDTO>> {
		if (request.term != 0) {
			return hashMapOf("interestRate" to listOf(rateService.getRate(location_code, request.term, request.principal, request.paymentType)))
		} else {
			val maturityDate = Date()
			maturityDate.time = request.maturityDate * 1000
			return hashMapOf("interestRate" to listOf(rateService.getRate(location_code, Date(), maturityDate, request.principal, request.paymentType)))
		}
	}

	protected fun formatError(error: String?) = hashMapOf<String, ErrorDTO>("error" to ErrorDTO(error?:"Unknown error"))

	@ExceptionHandler(IllegalStateException::class)
	protected fun handleIllegalState(ex: IllegalStateException, request: WebRequest) = ResponseEntity.status(HttpStatus.FORBIDDEN).body(formatError("Invalid user status"))

	@ExceptionHandler(IllegalArgumentException::class)
	protected fun handleIllegalArgument(ex: IllegalArgumentException, request: WebRequest) = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(formatError(ex.message))
}
