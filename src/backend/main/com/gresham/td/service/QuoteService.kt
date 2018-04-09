package com.gresham.td.service

import com.gresham.td.model.*
import com.gresham.td.model.dto.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.gresham.td.persistence.CustomerRepository
import com.gresham.td.persistence.QuoteRepository
import org.springframework.beans.factory.annotation.Value
import java.time.ZonedDateTime
import java.util.*


@Service
class QuoteService {
	@Autowired
	lateinit var customerRepository: CustomerRepository

	@Autowired
	lateinit var quoteRepository: QuoteRepository

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	@Value("\${quote.expiry.hour}")
	private val quoteExpiryHour: Int = 0

	@Value("\${quote.expiry.minute}")
	private val quoteExpiryMinute: Int = 0


	private fun generateReference(customer: Customer) : String {
		var uuid: String

		do {
			uuid = UUID.randomUUID().toString().toUpperCase()
			uuid = uuid.substring(uuid.length - 8)
		} while (customer.quotes.firstOrNull { it.reference == uuid } != null)

		return uuid
	}
	/**
	 * Creates a Term Deposit. This method will also schedule all the payments that are induced by the TD
	 * @param username login of the user making the request
	 * @param request the TD request
	 * @param locationCode the TD location code
	 * @return the created TD
	 */
	fun addQuote(username: String, locationCode: String, request: QuoteRequestDTO): QuoteDTO {
		// check the user has access to the location code
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		/* Create the TD object */
		val quote = request.toQuote()
		quote.closingDate =  Date(ZonedDateTime.now().withHour(quoteExpiryHour).withMinute(quoteExpiryMinute).toEpochSecond() * 1000)

		quote.reference = generateReference(customer)
		quote.customer = customer
		quoteRepository.save(quote)
		customer.quotes.add(quote)
		customerRepository.save(customer)
		return QuoteDTO(quote)
	}

	fun closeQuote(username: String, id: Long): QuoteDTO {
		val quote = quoteRepository.findOne(id) ?: throw IllegalArgumentException("Unknown quote")
		quote.status = QuoteStatus.Closed
		quoteRepository.save(quote)
		return QuoteDTO(quote)
	}

}
