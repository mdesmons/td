package com.gresham.td.service

import com.gresham.td.model.*
import com.gresham.td.model.dto.CloseTermDepositRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.gresham.td.model.dto.TermDepositDTO
import com.gresham.td.model.dto.TermDepositRequestDTO
import com.gresham.td.persistence.CustomerRepository
import com.gresham.td.persistence.TermDepositRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.io.*
import java.util.*


@Service
class TermDepositService {
	@Autowired
	lateinit var customerRepository: CustomerRepository

	@Autowired
	lateinit var termDepositRepository: TermDepositRepository

	@Autowired
	lateinit var interestService: InterestService

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	@Autowired
	lateinit var whtService: WHTService

	@Autowired
	lateinit var calendarService: CalendarService

	@Value("\${td.cache.notify.folder}")
	private val cacheOutputDir: String? = ""

	/**
	 * Prepares a DB record that represents the transfer from the NAT account to the NAT-TD.
	 * Note this method does not actually write in DB
	 * @param termDeposit the Term Deposit that causes this transfer
	 * @return the Transfer object
	 */
	private fun createTransferFromPrincipal(termDeposit: TermDeposit): Transfer {
		return Transfer(type = TransferType.principal,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = Date(),
				narrative = "Principal transfer",
				termDeposit = termDeposit)
	}

	/**
	 * Prepares DB records that represent an interest payment and associated WHT & Margin payments for a given term.
	 * Note this method does not actually write in DB
	 *
	 * This method is used to create the records in DB for an at-maturity interest payment, or for the last payment
	 * of a monthly interest TD
	 *
	 * @param termDeposit the Term Deposit that causes those transfers
	 * @param term the term in days for those payments
	 * @param date date at which the transfers are scheduled
	 * @return the list of created transfers
	 */
	private fun createTransfers(termDeposit: TermDeposit, term: Long, date: Date): List<Transfer> {
		val ret = mutableListOf<Transfer>()
		// interest payment from INT TD to CLI
		val interestPayment = Transfer(type = TransferType.interest,
				currency = termDeposit.currency,
				amount = Math.round(termDeposit.dailyNetClientInterest * term * 100.0) / 100.0,
				narrative = "Interest from INT TD to CLI",
				termDeposit = termDeposit,
				date = date)
		ret.add(interestPayment)

		if (termDeposit.dailyWHT != 0.0) {
			// WHT payment from INT TD to CLI
			val whtPayment = Transfer(type = TransferType.wht,
					currency = termDeposit.currency,
					amount = Math.round(termDeposit.dailyWHT * term * 100.0) / 100.0,
					narrative = "WHT payment from INT TD to WHT",
					termDeposit = termDeposit,
					date = date)
			ret.add(whtPayment)
		}

		if (termDeposit.dailyHaircut != 0.0) {
			// Haircut payment from INT TD to CLI Haircut
			val haircut = Transfer(type = TransferType.haircut,
					currency = termDeposit.currency,
					amount = Math.round(termDeposit.dailyHaircut * term * 100.0) / 100.0,
					narrative = "Haircut payment from CLI TD to CLI",
					termDeposit = termDeposit,
					date = date)
			ret.add(haircut)
		}
		return ret
	}

	/**
	 * Prepares DB records that represent the return of the Principal to NAT.
	 * Note this method does not actually write in DB
	 *
	 * @param termDeposit the Term Deposit that causes the transfer
	 * @param date date at which the transfer is scheduled
	 * @return the created transfer
	 */
	private fun createTransfersForPrincipalReturn(termDeposit: TermDeposit, date:Date): Transfer {
		return Transfer(type = TransferType.principalReturn,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = date,
				narrative = "Principal return from CLI TD to CLI",
				termDeposit = termDeposit)
	}

	/**
	 * Prepares DB records that represent the return of the Principal to NAT at a TD's maturity.
	 * Note this method does not actually write in DB
	 *
	 * @param termDeposit the Term Deposit that causes the transfer
	 * @return the created transfer
	 */
	private fun createTransfersForPrincipalReturn(termDeposit: TermDeposit): Transfer =
		createTransfersForPrincipalReturn(termDeposit, termDeposit.maturityDate)



	/**
	 * Prepares DB records that represent the return of the Principal to NAT at a TD's maturity + interest, WHT, margin payments.
	 * Note this method does not actually write in DB
	 *
	 * @param termDeposit the Term Deposit that causes the transfer
	 * @return the created transfer
	 */
	private fun createTransfersForAtCallPayment(termDeposit: TermDeposit): List<Transfer> {
		val ret = mutableListOf<Transfer>()

		// create final payment
		val transfers = createTransfers(termDeposit, termDeposit.term.toLong(), termDeposit.maturityDate)
		ret.addAll(transfers)

		// Principal return from TD to CLI
		ret.add(createTransfersForPrincipalReturn(termDeposit))

		return ret
	}



	private fun createTransfersForPeriodicPayment(termDeposit: TermDeposit, unit: Int, period: Int): List<Transfer> {
		val ret = mutableListOf<Transfer>()
		var date = Date(termDeposit.valueDate.time)
		var prevDate = date

		date = calendarService.addPeriod(date, unit, period)

		/* Schedule a set of payments each month */
		while (date.before(termDeposit.maturityDate)) {
			ret.addAll(createTransfers(termDeposit, calendarService.diffDays(date, prevDate), date))
			prevDate = date
			date = calendarService.addPeriod(date, unit, period)
		}

		// create final payment
		val lastPaymentTerm = calendarService.diffDays(termDeposit.maturityDate, prevDate)
		ret.addAll(createTransfers(termDeposit, lastPaymentTerm, termDeposit.maturityDate))

		// Principal return from TD to CLI
		ret.add(createTransfersForPrincipalReturn(termDeposit))

		return ret
	}

	/**
	 * Prepares DB records that represent the return of the Principal to NAT at a TD's maturity +
	 * interest, WHT, margin payments for monthly payments.
	 * Note this method does not actually write in DB
	 *
	 * @param termDeposit the Term Deposit that causes the transfer
	 * @return the created transfer
	 */
	private fun createTransfersForMonthlyPayment(termDeposit: TermDeposit): List<Transfer> =
			createTransfersForPeriodicPayment(termDeposit, Calendar.MONTH, 1)

	private fun createTransfersForQuarterlyPayment(termDeposit: TermDeposit): List<Transfer> =
			createTransfersForPeriodicPayment(termDeposit, Calendar.MONTH, 3)

	private fun createTransfersForHalfYearlyPayment(termDeposit: TermDeposit): List<Transfer>  =
			createTransfersForPeriodicPayment(termDeposit, Calendar.MONTH, 6)

	private fun createTransfersForYearlyPayment(termDeposit: TermDeposit): List<Transfer>  =
			createTransfersForPeriodicPayment(termDeposit, Calendar.YEAR, 1)

	/**
	 * Creates a Term Deposit. This method will also schedule all the payments that are induced by the TD
	 * @param username login of the user making the request
	 * @param request the TD request
	 * @param locationCode the TD location code
	 * @return the created TD
	 */
	fun addTermDeposit(username: String, locationCode: String, request: TermDepositRequestDTO): TermDepositDTO {
		// check the user has access to the location code
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		/* Create the TD object */
		val termDeposit = request.toTermDeposit()
		termDeposit.customer = customer

		/* calculate the value date (date at which the TD starts accruing interest. If the TD is opened on a business day,
		it accrues interest immediately. Otherwise it accrues interest from the next business day
		 */

		termDeposit.openingDate = Date()
		if (calendarService.isBusinessDay(termDeposit.openingDate)) {
			termDeposit.valueDate = termDeposit.openingDate
		} else {
			termDeposit.valueDate = calendarService.nextBusinessDay(termDeposit.openingDate)
		}

		/* if the user specified a maturity date, calculate the term.
			Otherwise use the term provided to calculate the maturity date
		 */
		if (request.maturity != 0L) {
			termDeposit.maturityDate = Date(request.maturity)
			termDeposit.term = calendarService.diffDays(termDeposit.maturityDate, termDeposit.valueDate)
		} else {
			termDeposit.maturityDate = calendarService.addPeriod(termDeposit.valueDate, Calendar.DAY_OF_MONTH, termDeposit.term.toInt())
		}

		/* We use the interest provided by the bank, unless the TD was opened by the desk and the operator provided
		an explicit rate
		 */
		if (request.interest == 0.0) {
			// calculate interest
			termDeposit.interest = interestService.getRate(username, locationCode, termDeposit.term, termDeposit.paymentType).value
		} else {
			/* If a rate was provided, make sure the user is a Desk user */
			val user = userDetailsService.loadUserByUsername(username)
			if (user.authorities.contains(SimpleGrantedAuthority(UserCategory.desk.toString()))) {
				termDeposit.interest = request.interest
			} else {
				throw SecurityException("Only desk users can specify an interest rate")
			}
		}

		// daily gross interest accrued on the account (ie money we will request from CACHE)
		termDeposit.dailyGrossCustomerInterest = termDeposit.interest * termDeposit.principal / (360.0 * 100.0)

		// daily customer profit
		termDeposit.dailyHaircut = termDeposit.haircut * termDeposit.principal / (360.0 * 100.0)

		// daily interest minus customer profit
		termDeposit.dailyGrossClientInterest = termDeposit.dailyGrossCustomerInterest - termDeposit.dailyHaircut

		// daily WHT
		val whtRate = whtService.getRate(termDeposit.sourceAccount)
		termDeposit.dailyWHT = termDeposit.dailyGrossClientInterest * whtRate / 100.0

		// final interest paid to client
		termDeposit.dailyNetClientInterest = termDeposit.dailyGrossClientInterest - termDeposit.dailyWHT

		/* create the transfers for the TD:
			- transfer of principal from CLI to TD
			- transfer of interest back to CLI at maturity or monthly
			- payment of WHT from CLI at maturity or monthly
			- payment of margin to the customer margin account (if relevant) at maturity or monthly
			- transfer of principal back to CLI at maturity
		 */

		termDeposit.transfers.add(createTransferFromPrincipal(termDeposit))

		when (termDeposit.paymentType) {
			TermDepositPaymentType.AtMaturity -> termDeposit.transfers.addAll(createTransfersForAtCallPayment(termDeposit))
			TermDepositPaymentType.Monthly -> termDeposit.transfers.addAll(createTransfersForMonthlyPayment(termDeposit))
			TermDepositPaymentType.Quarterly -> termDeposit.transfers.addAll(createTransfersForQuarterlyPayment(termDeposit))
			TermDepositPaymentType.HalfYearly -> termDeposit.transfers.addAll(createTransfersForHalfYearlyPayment(termDeposit))
			TermDepositPaymentType.Yearly -> termDeposit.transfers.addAll(createTransfersForYearlyPayment(termDeposit))
		}

		termDepositRepository.save(termDeposit)
		customer.termDeposits.add(termDeposit)
		customerRepository.save(customer)

		return TermDepositDTO(termDeposit)
	}

	fun closeTermDeposit(username: String, id: Long, request: CloseTermDepositRequestDTO): TermDepositDTO {
		val termDeposit = termDepositRepository.findOne(id) ?: throw IllegalArgumentException("Unknown term deposit")

		if (termDeposit.status != TermDepositStatus.Opened) {
			throw IllegalStateException("TD is not open")

		}
		userDetailsService.canAccessLocation(username, termDeposit.customer.locationCode) || throw IllegalArgumentException("Permission error")

		// financial hardship: nuke pending transactions, reimburse principal
		// system: nuke pending transactions, reimburse principal
		// notice period: if AtCall, reject. Otherwise nuke transactions & create last monthly interest (w/ penalty), reimburse principal
		// else: error

		// TODO: Make sure no interest was paid
		/* TODO : Check dates. If closing on a non business day, the principal return will be on the next business day, and the closing date will then be next-next-business day
		 */
		if (request.reason in listOf(TermDepositCloseReason.FinancialHardship, TermDepositCloseReason.System)) {
			termDeposit.transfers.filter { it.type != TransferType.principal }.forEach { it.status = TransferStatus.cancelled }
			termDeposit.transfers.add(createTransfersForPrincipalReturn(termDeposit, Date()))

			// the principal is returned, and we'll receive confirmation in the next day BTR, then only can we close the TD
			termDeposit.closingDate = calendarService.nextBusinessDay(Date())
			termDeposit.reasonForClose = request.reason
			termDeposit.status = TermDepositStatus.PendingClosed
			termDepositRepository.save(termDeposit)
		} else if (request.reason == TermDepositCloseReason.NoticePeriod) {
			// check the end of notice period is not post maturity
			var endOfNoticePeriod = Date()
			endOfNoticePeriod = calendarService.addPeriod(endOfNoticePeriod, Calendar.DAY_OF_MONTH, 31)
			if (termDeposit.maturityDate.before(endOfNoticePeriod)) {
				// error
			} else {
				// TODO create last payment, with interest penalty
				termDeposit.transfers.forEach { it.status = TransferStatus.cancelled }
				termDeposit.transfers.add(createTransfersForPrincipalReturn(termDeposit, endOfNoticePeriod))

				// the principal is returned, and we'll receive confirmation in the next day BTR, then only can we close the TD
				termDeposit.closingDate = calendarService.nextBusinessDay(endOfNoticePeriod)
				termDeposit.reasonForClose = request.reason
				termDeposit.status = TermDepositStatus.PendingClosed
				termDepositRepository.save(termDeposit)
			}
		} else {
			// return an error
		}

		return TermDepositDTO(termDeposit)
	}

	/* Close all TDs that are marked as PendingClose, and whose close date is today or before (aka technical close) */
	@Scheduled(cron = "\${td.close.schedule}")
	fun closePendingTermDepositsImpl(): List<TermDepositDTO> {
		var tomorrow = Date()
		tomorrow = calendarService.addPeriod(tomorrow, Calendar.DAY_OF_MONTH, 1)

		val termDeposits = termDepositRepository.findByStatus(TermDepositStatus.PendingClosed).filter { it.technicalClosingDate.before(tomorrow) }
		termDeposits.forEach { it.status = TermDepositStatus.Closed }
		termDepositRepository.save(termDeposits)
		return termDeposits.map { TermDepositDTO(it) }
	}

	/* Close all TDs that are marked as PendingClose, and whose close date is today or before */
	fun closePendingTermDeposits(username: String): List<TermDepositDTO> {
		userDetailsService.hasAuthority(username, UserCategory.managedServices)  || throw IllegalArgumentException("Permission error")
		return closePendingTermDepositsImpl()
	}

	@Scheduled(cron = "\${td.mature.schedule}")
	fun matureTermDepositsImpl(): List<TermDepositDTO> {
		var tomorrow = Date()
		tomorrow = calendarService.addPeriod(tomorrow, Calendar.DAY_OF_MONTH, 1)

		val termDeposits = termDepositRepository.findByStatus(TermDepositStatus.Opened).filter { it.maturityDate.before(tomorrow) }
		termDeposits.forEach { it.status = TermDepositStatus.PendingClosed }
		termDepositRepository.save(termDeposits)
		return termDeposits.map { TermDepositDTO(it) }
	}

	/* Mature all TDs */
	fun matureTermDeposits(username: String): List<TermDepositDTO> {
		userDetailsService.hasAuthority(username, UserCategory.managedServices)  || throw IllegalArgumentException("Permission error")
		return matureTermDepositsImpl()
	}

	// notify CACHE of the accrued interest
	@Scheduled(cron = "\${td.cache.notify.schedule}")
	fun notifyCacheImpl(): Map<String, Double> {
		val now = Date()
		val termDepositsGroupedByCustomer = termDepositRepository.findByStatus(TermDepositStatus.Opened).groupBy { it.customer.locationCode }
		val amounts = termDepositsGroupedByCustomer.mapValues{ entry -> entry.value.sumByDouble { it.principal * it.interest *  calendarService.diffDays(now, it.valueDate) / 100.00 }  }

		val bw = BufferedWriter(FileWriter(cacheOutputDir + File.separator + "cache.csv"))
		amounts.forEach { locationCode, value -> bw.write(locationCode + "," + value.toString()); bw.newLine() }
		bw.close()

		return amounts
	}

	fun notifyCache(username: String): Map<String, Double>  {
		userDetailsService.hasAuthority(username, UserCategory.managedServices)  || throw IllegalArgumentException("Permission error")
		return notifyCacheImpl()
	}
}
