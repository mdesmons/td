package com.gresham.td.service

import com.gresham.td.model.TermDepositPaymentType
import com.gresham.td.model.dto.RateDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class InterestService {
	@Autowired
	lateinit var calendarService: CalendarService

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	private val logger = LoggerFactory.getLogger(InterestService::class.java)

	fun getRate(username: String, locationCode: String, term: Int, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		/* some bogus formula here: 0.1% per slice of 10k principal, 0.1% per month term, penalty for monthly interest */
		var rate = 0.001 * (principal / 10000).toInt() + 0.001 * (term / 30)
		if (paymentType == TermDepositPaymentType.monthly) {
			rate -= 0.002
		}

		rate = Math.round(rate * 10000.0) / 100.0
		logger.info("Rate request for term of " + term + " days, principal " + principal + ". Calculated rate: " + rate)
		return RateDTO(value = rate)
	}

	fun getRate(username: String, locationCode: String, openingDate: Date, maturity: Date, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		var valueDate = openingDate

		if (!calendarService.isBusinessDay(openingDate)) {
			valueDate = calendarService.nextBusinessDay(openingDate)
		}

		val term = calendarService.diffDays(maturity, valueDate)
		return getRate(username, locationCode,  term, principal, paymentType)
	}

}
