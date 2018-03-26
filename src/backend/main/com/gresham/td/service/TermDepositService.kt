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

	private fun createTransferFromPrincipal(termDeposit: TermDeposit): Transfer {
		return Transfer(type = TransferType.principal,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = Date(),
				narrative = "Principal transfer",
				termDeposit = termDeposit)
	}


	private fun createTransfers(termDeposit: TermDeposit, term: Long): List<Transfer> {
		val ret = mutableListOf<Transfer>()
		// interest payment from INT TD to CLI
		val interestPayment = Transfer(type = TransferType.interest,
				currency = termDeposit.currency,
				amount = Math.round(termDeposit.dailyNetClientInterest * term * 100.0) / 100.0,
				narrative = "Interest from INT TD to CLI",
				termDeposit = termDeposit)
		ret.add(interestPayment)

		if (termDeposit.dailyWHT != 0.0) {
			// WHT payment from INT TD to CLI
			val whtPayment = Transfer(type = TransferType.wht,
					currency = termDeposit.currency,
					amount = Math.round(termDeposit.dailyWHT * term * 100.0) / 100.0,
					narrative = "WHT payment from INT TD to WHT",
					termDeposit = termDeposit)
			ret.add(whtPayment)
		}

		if (termDeposit.dailyHaircut != 0.0) {
			// Haircut payment from INT TD to CLI Haircut
			val haircut = Transfer(type = TransferType.haircut,
					currency = termDeposit.currency,
					amount = Math.round(termDeposit.dailyHaircut * term * 100.0) / 100.0,
					narrative = "Haircut payment from CLI TD to CLI",
					termDeposit = termDeposit)
			ret.add(haircut)
		}
		return ret
	}

	private fun createTransfersForPrincipalReturn(termDeposit: TermDeposit, date:Date? = null): Transfer {
		val closeDate = date?:termDeposit.maturityDate
		return Transfer(type = TransferType.principalReturn,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = closeDate,
				narrative = "Principal return from CLI TD to CLI",
				termDeposit = termDeposit)
	}

	private fun createTransfersForAtCallPayment(termDeposit: TermDeposit): List<Transfer> {
		val ret = mutableListOf<Transfer>()

		// create final payment
		val transfers = createTransfers(termDeposit, termDeposit.term.toLong())
		transfers.forEach { it.date = termDeposit.maturityDate }
		ret.addAll(transfers)

		// Principal return from TD to CLI
		ret.add(createTransfersForPrincipalReturn(termDeposit))

		return ret
	}

	private fun createTransfersForMonthlyPayment(termDeposit: TermDeposit): List<Transfer> {
		val ret = mutableListOf<Transfer>()
		var date = Date(termDeposit.valueDate.time)

		date = calendarService.addDays(date, 30)

		while (date.before(termDeposit.maturityDate)) {
			val transfers = createTransfers(termDeposit, 30)
			transfers.forEach { it.date = date }
			ret.addAll(transfers)
			date = calendarService.addDays(date, 30)
		}

		// create final payment
		val lastPaymentTerm = termDeposit.term.rem(30).toLong()
		val transfers = createTransfers(termDeposit, lastPaymentTerm)
		transfers.forEach { it.date = termDeposit.maturityDate }
		ret.addAll(transfers)

		// Principal return from TD to CLI
		ret.add(createTransfersForPrincipalReturn(termDeposit))

		return ret
	}

	fun addTermDeposit(username: String, locationCode: String, request: TermDepositRequestDTO): TermDepositDTO {
		// check the user has access to the location code
		userDetailsService.canAccessLocation(username, locationCode) || throw IllegalArgumentException("Permission error")
		val customer = customerRepository.findByLocationCode(locationCode) ?: throw IllegalArgumentException("Customer not found")

		val user = userDetailsService.loadUserByUsername(username)
		val termDeposit = request.toTermDeposit()
		termDeposit.customer = customer

		// set dates
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
			termDeposit.maturityDate = calendarService.addDays(termDeposit.valueDate, termDeposit.term)
		}

		if (request.interest == 0.0) {
			// calculate interest
			termDeposit.interest = interestService.getRate(username, locationCode, termDeposit.term, termDeposit.principal, termDeposit.paymentType).value
		} else {
			if (user.authorities.contains(SimpleGrantedAuthority(UserCategory.desk.toString()))) {
				termDeposit.interest = request.interest
			} else {
				throw SecurityException("Only desk users can specify an interest rate")
			}
		}

		// daily gross interest accrued on the account (ie money we'll request from CACHE)
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

		termDeposit.transfers.add(createTransferFromPrincipal(termDeposit))

		// create transfers
		if (termDeposit.paymentType == TermDepositPaymentType.AtMaturity) {
			termDeposit.transfers.addAll(createTransfersForAtCallPayment(termDeposit))
		} else {
			termDeposit.transfers.addAll(createTransfersForMonthlyPayment(termDeposit))
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
			endOfNoticePeriod = calendarService.addDays(endOfNoticePeriod, 31)
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
		tomorrow = calendarService.addDays(tomorrow, 1)

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
		tomorrow = calendarService.addDays(tomorrow, 1)

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
