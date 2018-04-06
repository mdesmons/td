package com.gresham.td.service

import com.gresham.td.model.Customer
import com.gresham.td.model.TermDepositPaymentType
import com.gresham.td.model.dto.RateDTO
import com.gresham.td.persistence.CustomerRepository
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

/**
 * This class calculate the interest for a customer's TD
 * based on the customer, the TD term and payment type
 */
class Rate(var termMin: Long = 0,
		   var termMax: Long = Long.MAX_VALUE,
		   val rate: Double) {

	override fun toString(): String {
		val sb = StringBuilder()
		sb.append("min term=" )
		sb.append(termMin)
		sb.append(", " )
		sb.append("max term=" )
		sb.append(if (termMax == Long.MAX_VALUE) { "<inf>" } else {termMax} )
		sb.append(", " )
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

	@Autowired
	lateinit var customerRepository: CustomerRepository

	@Value("\${rate-file.folder}")
	private val rateFileFolder: String? = null

	private val rates : MutableList<Rate> = mutableListOf()
	private val paymentTypeModifiers : MutableMap<TermDepositPaymentType, Double> = mutableMapOf()


	private val logger = LoggerFactory.getLogger(InterestService::class.java)


	/**
	 * Reads the initial rate file
	 */
	override fun afterPropertiesSet() {
		loadRateFile()
		loadPaymentTypeModifierFile()
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
			var minTerm = 0L

			for (record in records) {
				val rate = Rate(rate = record["rate"].toDouble(), termMax = record["term"].toLong(), termMin = minTerm)
				minTerm = rate.termMax + 1
				rates.add(rate)
			}
			reader.close()
		} else {
			logger.warn("Unable to read rate file")
		}
	}

	/**
	 * Loads a rate spreadsheet (CSV) at the configured interval and populate the rates table
	 */
	@Scheduled(cron = "\${rate-file.schedule}")
	private fun loadPaymentTypeModifierFile() {
		logger.info("Loading interest rate payment type modifier file")
		paymentTypeModifiers.clear()
		val ratesFile = File(rateFileFolder + File.separator + "paymentTypeModifiers.csv")

		if (Files.isRegularFile(ratesFile.toPath())) {
			val reader = FileReader(ratesFile)
			val records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader)
			for (record in records) {
				val paymentType = TermDepositPaymentType.valueOf(record["paymentType"])
				val modifier = record["modifier"].toDouble()
				paymentTypeModifiers.put(paymentType, modifier)
			}
			reader.close()
		} else {
			logger.warn("Unable to read interest rate payment type modifier file")
		}
	}

	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param customer the customer object
	 * @param term the TD term in days
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object or null if we couldn't find any rate for the requested parameters
	 * @exception IllegalArgumentException if we found several possible rates for the TD
 	 */
	private fun getRateImpl(customer: Customer, term: Long, paymentType: TermDepositPaymentType): Rate? {
		val rate = rates.find { term in (it.termMin .. it.termMax)} ?: return null
		val modifier = paymentTypeModifiers[paymentType] ?: return null

		return Rate(rate.termMin, rate.termMax, rate.rate + modifier - customer.margin)
	}


	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param username user making the request
	 * @param locationCode the customer location code
	 * @param term the TD term in days
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object
	 * @exception IllegalArgumentException if we couldn't find any rate for the requested parameters
	 */
	fun getRate(username: String, locationCode: String, term: Long, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Unknown customer")

		/* Get the rate for this customer/TD. If there isn't any customer-specific rate, get the default one */
		val rate = getRateImpl(customer , term, paymentType)

		return if (rate != null) { RateDTO(rate.rate) } else { throw IllegalArgumentException("Cannot determine rate") }
	}

	/**
	 * Calculates the interest rate for a given TD and customer
	 * @param username user making the request
	 * @param locationCode the customer location code
	 * @param openingDate the TD opening date
	 * @param maturity the TD maturity date
	 * @param paymentType the TD interest payment type, whether monthly or at maturity
	 * @return a rate object
	 * @exception IllegalArgumentException if we couldn't find any rate for the requested parameters
	 */
	fun getRate(username: String, locationCode: String, openingDate: Date, maturity: Date, paymentType: TermDepositPaymentType): RateDTO {
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")

		var valueDate = openingDate

		/* Calculate the TD length */
		if (!calendarService.isBusinessDay(openingDate)) {
			valueDate = calendarService.nextBusinessDay(openingDate)
		}
		val term = calendarService.diffDays(maturity, valueDate)

		/* Get the corresponding rate */
		return getRate(username, locationCode,  term, paymentType)
	}
}
