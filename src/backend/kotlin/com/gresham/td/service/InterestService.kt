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

	private val logger = LoggerFactory.getLogger(InterestService::class.java)

	fun getRate(locationCode: String, term: Int, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		/* some bogus formula here: 0.1% per slice of 10k principal, 0.1% per month term, penalty for monthly interest */
		var rate = 0.001 * (principal / 10000).toInt() + 0.001 * (term / 30)
		if (paymentType == TermDepositPaymentType.atMaturity) return RateDTO(rate * 100)
		return RateDTO(value = (rate - 0.002) * 100)
	}

	fun getRate(locationCode: String, openingDate: Date, maturityDate: Date, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		var valueDate = openingDate

		if (!calendarService.isBusinessDay(openingDate)) {
			valueDate = calendarService.nextBusinessDay(openingDate)
		}

		val term = calendarService.diffDays(maturityDate, valueDate)
		return getRate(locationCode,  term, principal, paymentType)
	}

}
