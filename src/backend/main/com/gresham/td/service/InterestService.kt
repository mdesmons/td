package com.gresham.td.service

import com.gresham.td.model.TermDepositPaymentType
import com.gresham.td.model.dto.RateDTO
import org.apache.commons.collections4.MultiValuedMap
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import org.apache.commons.csv.CSVFormat
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.text.DecimalFormat

class Rate(var amountMin: Double = 0.0,
		   var amountMax: Double = Double.MAX_VALUE,
		   var termMin: Int = 0,
		   var termMax: Int = Int.MAX_VALUE,
		   val rate: Double,
		   var type: TermDepositPaymentType = TermDepositPaymentType.AtMaturity) {

	override fun toString(): String {
		val amtFormat = DecimalFormat("#,###.00")
		amtFormat.minimumIntegerDigits = 1
		val sb = StringBuilder("min amount=" + amtFormat.format(amountMin) + " ")
		sb.append("max amount=" )
		sb.append(if (amountMax == Double.MAX_VALUE) { "<inf>" } else {amtFormat.format(amountMax)} )
		sb.append(", " )
		sb.append("min term=" )
		sb.append(termMin)
		sb.append(", " )
		sb.append("max term=" )
		sb.append(if (termMax == Int.MAX_VALUE) { "<inf>" } else {termMax} )
		sb.append(", " )
		sb.append("type=" )
		sb.append(type.toString())
		sb.append(", " )
		sb.append("rate=" )
		sb.append(rate)

		return sb.toString()
	}
}

@Service
@Configuration
class InterestService : InitializingBean {
	@Autowired
	lateinit var calendarService: CalendarService

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	@Value("\${rate-file.folder}")
	private val rateFileFolder: String? = null

	private val rates : MultiValuedMap<String, Rate> = ArrayListValuedHashMap()

	private val logger = LoggerFactory.getLogger(InterestService::class.java)


	/**
	 * Reads the initial rate file
	 */
	override fun afterPropertiesSet() {
		loadRateFile()
	}

	/**
	 * Loads a rate spreadsheet (CSV) at the configured interval and populate the rates table
	 */
	@Scheduled(cron = "\${rate-file.schedule}")
	private fun loadRateFile() {
		logger.info("Loading interest rate file")
		rates.clear()
		val ratesFile = File(rateFileFolder + File.separator + "rates.csv")

		if (Files.isRegularFile(ratesFile.toPath())) {
			val reader = FileReader(ratesFile)
			val records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader)
			for (record in records) {
				val rate = Rate(rate = record["rate"].toDouble())
				if (record["amountMin"].isNotBlank()) {
					rate.amountMin = record["amountMin"].toDouble()
				}
				if (record["amountMax"].isNotBlank()) {
					rate.amountMax = record["amountMax"].toDouble()
				}
				if (record["termMin"].isNotBlank()) {
					rate.termMin = record["termMin"].toInt()
				}
				if (record["termMax"].isNotBlank()) {
					rate.termMax = record["termMax"].toInt()
				}
				when (record["type"].toUpperCase()) {
					"ATCALL", "AT-CALL", "AT CALL" -> rate.type = TermDepositPaymentType.AtMaturity
					"MONTHLY" -> rate.type = TermDepositPaymentType.Monthly
				}

				rates.put(record["locationCode"], rate)
				logger.debug("Location=" + if (record["locationCode"].isEmpty()) {
					"DEFAULT"
				} else {
					record["locationCode"]
				} + ": " + rate.toString())
			}
		} else {
			logger.warn("Unable to read rate file")
		}
	}

	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param locationCode the customer location code
	 * @param term the TD term in days
	 * @param principal the TD principal in dollars
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object or null if we couldn't find any rate for the requested parameters
	 * @exception IllegalArgumentException if we found several possible rates for the TD
 	 */
	private fun getRateImpl(locationCode: String, term: Int, principal: Double, paymentType: TermDepositPaymentType): Rate? {
		if(rates.containsKey(locationCode)) {
			val rates = rates[locationCode].filter {
				principal in (it.amountMin .. it.amountMax) &&
						term in (it.termMin .. it.termMax) &&
						paymentType == it.type
			}

			if (rates.size == 1) {
				return rates.first()
			}

			if (rates.size > 1) {
				logger.error("Several rates found for TD request. Please check the rate file")
				rates.forEach { logger.info(it.toString()) }
				throw IllegalArgumentException("Cannot determine rate")
			}
		}
		return null
	}


	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param username user making the request
	 * @param locationCode the customer location code
	 * @param term the TD term in days
	 * @param principal the TD principal in dollars
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object
	 * @exception IllegalArgumentException if we couldn't find any rate for the requested parameters
	 */
	fun getRate(username: String, locationCode: String, term: Int, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")

		/* Get the rate for this customer/TD. If there isn't any customer-specific rate, get the default one */
		val rate = getRateImpl(locationCode , term, principal, paymentType) ?:
				getRateImpl("", term, principal, paymentType)

		return if (rate != null) { RateDTO(rate.rate) } else { throw IllegalArgumentException("Cannot determine rate") }
	}

	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param username user making the request
	 * @param locationCode the customer location code
	 * @param openingDate the TD opening date
	 * @param maturity the TD maturity date
	 * @param principal the TD principal in dollars
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object
	 * @exception IllegalArgumentException if we couldn't find any rate for the requested parameters
	 */
	fun getRate(username: String, locationCode: String, openingDate: Date, maturity: Date, principal: Double, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		var valueDate = openingDate

		/* Calculate the TD length */
		if (!calendarService.isBusinessDay(openingDate)) {
			valueDate = calendarService.nextBusinessDay(openingDate)
		}
		val term = calendarService.diffDays(maturity, valueDate)

		/* Get the corresponding rate */
		return getRate(username, locationCode,  term, principal, paymentType)
	}
}
