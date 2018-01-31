package com.gresham.td.service

import com.gresham.td.model.*
import com.gresham.td.model.dto.CloseTermDepositRequestDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import com.gresham.td.model.dto.TermDepositDTO
import com.gresham.td.model.dto.TermDepositRequestDTO
import com.gresham.td.persistence.CustomerRepository
import com.gresham.td.persistence.TermDepositRepository
import com.gresham.td.persistence.TransferRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.security.Principal
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

	private fun createTransferFromPrincipal(termDeposit: TermDeposit): Transfer {
		val ret = Transfer(type = TransferType.principal,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = Date(),
				narrative = "Principal transfer",
				termDeposit = termDeposit)
		return ret
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

	private fun createTransfersForPrincipalReturn(termDeposit: TermDeposit): Transfer {
		val principalReturnPayment = Transfer(type = TransferType.principalReturn,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = termDeposit.maturityDate,
				narrative = "Principal return from CLI TD to CLI",
				termDeposit = termDeposit)
		return principalReturnPayment
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
		var lastPaymentTerm = termDeposit.term.rem(30).toLong()
		val transfers = createTransfers(termDeposit, lastPaymentTerm)
		transfers.forEach { it.date = termDeposit.maturityDate }
		ret.addAll(transfers)

		// Principal return from TD to CLI
		ret.add(createTransfersForPrincipalReturn(termDeposit))

		return ret
	}

	// TODO check the user has access to the location code
	fun addTermDeposit(principal: Principal?, locationCode: String, request: TermDepositRequestDTO): TermDepositDTO {
		if (principal == null) {
			throw SecurityException("Unknown user")
		}

		val user = userDetailsService.loadUserByUsername(principal.name)

		val customer = customerRepository.findByLocationCode(locationCode)
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
			termDeposit.interest = interestService.getRate(locationCode, termDeposit.term, termDeposit.principal, termDeposit.paymentType).value
		} else {
			if (user.authorities.contains(SimpleGrantedAuthority("desk"))) {
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
		val whtRate = whtService.getRate()
		termDeposit.dailyWHT = termDeposit.dailyGrossClientInterest * whtRate / 100.0

		// final interest paid to client
		termDeposit.dailyNetClientInterest = termDeposit.dailyGrossClientInterest - termDeposit.dailyWHT

		termDeposit.transfers.add(createTransferFromPrincipal(termDeposit))

		// create transfers
		if (termDeposit.paymentType == TermDepositPaymentType.atMaturity) {
			termDeposit.transfers.addAll(createTransfersForAtCallPayment(termDeposit))
		} else {
			termDeposit.transfers.addAll(createTransfersForMonthlyPayment(termDeposit))
		}

		termDepositRepository.save(termDeposit)
		customer.termDeposits.add(termDeposit)
		customerRepository.save(customer)

		return TermDepositDTO(termDeposit)
	}

	// TODO check the user has access to the location code
	fun closeTermDeposit(principal: Principal?, id: Long, request: CloseTermDepositRequestDTO): TermDepositDTO {
		var termDeposit = termDepositRepository.findOne(id)
		if (termDeposit == null) {
			// error
		}

		if (termDeposit.status != TermDepositStatus.opened) {
			// error

		}

		// financial hardship: nuke pending transactions, reimburse principal
		// system: nuke pending transactions, reimburse principal
		// notice period: if AtCall, reject. Otherwise nuke transactions & create last monthly interest (w/ penalty), reimburse principal
		// else: error

		// TODO: Make sure no interest was paid
		/* TODO : Check dates. If closing on a non business day, the principal return will be on the next business day, and the closing date will then be next-next-business day
		 */
		if (request.reason in listOf(TermDepositCloseReason.financialHardship, TermDepositCloseReason.system)) {
			termDeposit.transfers.filter { it.type != TransferType.principal }.forEach { it.status = TransferStatus.cancelled }
			termDeposit.transfers.add(createTransfersForPrincipalReturn(termDeposit))

			// the principal is returned, and we'll receive confirmation in the next day BTR, then only can we close the TD
			termDeposit.closingDate = calendarService.nextBusinessDay(Date())
			termDeposit.reasonForClose = request.reason
			termDeposit.status = TermDepositStatus.pendingClosed
			termDepositRepository.save(termDeposit)
		} else if (request.reason == TermDepositCloseReason.noticePeriod) {
			// check the end of notice period is not post maturity
			var endOfNoticePeriod = Date()
			endOfNoticePeriod = calendarService.addDays(endOfNoticePeriod, 31)
			if (termDeposit.maturityDate.before(endOfNoticePeriod)) {
				// error
			} else {
				// TODO create last payment, with interest penalty
				termDeposit.transfers.forEach { it.status = TransferStatus.cancelled }
				termDeposit.transfers.add(createTransfersForPrincipalReturn(termDeposit))

				// the principal is returned, and we'll receive confirmation in the next day BTR, then only can we close the TD
				termDeposit.closingDate = calendarService.nextBusinessDay(endOfNoticePeriod)
				termDeposit.reasonForClose = request.reason
				termDeposit.status = TermDepositStatus.pendingClosed
				termDepositRepository.save(termDeposit)
			}
		} else {
			// return an error
		}

		return TermDepositDTO(termDeposit)
	}

	// TODO check credentials
	/* Close all TDs that are marked as PendingClose, and whose close date is today or before */
	fun closePendingTermDeposits(principal: Principal?): List<TermDepositDTO> {
		var tomorrow = Date()
		tomorrow = calendarService.addDays(tomorrow, 1)

		var termDeposits = termDepositRepository.findByStatus(TermDepositStatus.pendingClosed).filter { it.closingDate.before(tomorrow) }
		termDeposits.forEach { it.status = TermDepositStatus.closed }
		termDepositRepository.save(termDeposits)
		return termDeposits.map { TermDepositDTO(it) }
	}
}
