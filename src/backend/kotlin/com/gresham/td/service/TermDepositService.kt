package com.gresham.td.service

import com.gresham.td.model.TermDeposit
import com.gresham.td.model.TermDepositPaymentType
import com.gresham.td.model.Transfer
import com.gresham.td.model.TransferType
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
	lateinit var transferRepository: TransferRepository

	@Autowired
	lateinit var interestService: InterestService

	@Autowired
	lateinit var userDetailsService: UserDetailsServiceImpl

	@Autowired
	lateinit var whtService: WHTService

	@Autowired
	lateinit var calendarService: CalendarService

	fun createTransferFromPrincipal(termDeposit: TermDeposit) : Transfer {
		val ret = Transfer(type = TransferType.principal,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = Date(),
				narrative = "Principal transfer",
				termDeposit = termDeposit)
		return ret
	}

	fun createTransfersForMaturityPayment(termDeposit: TermDeposit) : List<Transfer> {
		val ret = mutableListOf<Transfer>()
		val maturityDate = calendarService.addDays(termDeposit.valueDate, termDeposit.term)
		var transferDate = maturityDate;
		if (!calendarService.isBusinessDay(maturityDate)) {
			transferDate = calendarService.nextBusinessDay(transferDate)
		}
		// interest payment from INT TD to CLI
		val interestPayment = Transfer(type = TransferType.interest,
				currency = termDeposit.currency,
				amount = termDeposit.dailyNetClientInterest * termDeposit.term,
				date = transferDate,
				narrative = "Interest from INT TD to CLI",
				termDeposit = termDeposit)
		ret.add(interestPayment)

		// WHT payment from INT TD to CLI
		val whtPayment = Transfer(type = TransferType.wht,
				currency = termDeposit.currency,
				amount = termDeposit.dailyWHT * termDeposit.term,
				date = transferDate,
				narrative = "WHT payment from INT TD to WHT",
				termDeposit = termDeposit)
		ret.add(whtPayment)

		// Principal return from TD to CLI
		val principalReturnPayment = Transfer(type = TransferType.principal,
				currency = termDeposit.currency,
				amount = termDeposit.principal,
				date = transferDate,
				narrative = "Principal return from CLI TD to CLI",
				termDeposit = termDeposit)
		ret.add(principalReturnPayment)

		// Haircut payment from INT TD to CLI Haircut
		val haircut = Transfer(type = TransferType.haircut,
				currency = termDeposit.currency,
				amount = termDeposit.dailyHaircut * termDeposit.term,
				date = transferDate,
				narrative = "Haircut payment from CLI TD to CLI",
				termDeposit = termDeposit)
		ret.add(haircut)

		return ret
	}

	fun createTransfersForMonthlyPayment(termDeposit: TermDeposit) : List<Transfer> {
		val ret = listOf<Transfer>()
		return ret
	}

	fun addTermDeposit(principal: Principal?, locationCode: String, request: TermDepositRequestDTO) : TermDepositDTO {
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
		if (request.maturityDate != 0L) {
			termDeposit.maturityDate = Date()
			termDeposit.maturityDate.time = request.maturityDate

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
		termDeposit.dailyHaircut = termDeposit.haircut  * termDeposit.principal / (360.0 * 100.0)

		// daily interest minus customer profit
		termDeposit.dailyGrossClientInterest = termDeposit.dailyGrossCustomerInterest - termDeposit.dailyHaircut

		// daily WHT
		val whtRate = whtService.getRate()
		termDeposit.dailyWHT = termDeposit.dailyGrossClientInterest * whtRate / 100.0

		// final interest paid to client
		termDeposit.dailyNetClientInterest = termDeposit.dailyGrossClientInterest - termDeposit.dailyWHT

		val transfer = createTransferFromPrincipal(termDeposit)
		termDeposit.transfers.add(transfer)

		// create transfers
		if (termDeposit.paymentType == TermDepositPaymentType.atMaturity) {
			val transactions = createTransfersForMaturityPayment(termDeposit)
			termDeposit.transfers.addAll(transactions)
		} else {
			val transactions = createTransfersForMonthlyPayment(termDeposit)
			termDeposit.transfers.addAll(transactions)
		}

		termDepositRepository.save(termDeposit)
		customer.termDeposits.add(termDeposit)
		customerRepository.save(customer)

		return TermDepositDTO(termDeposit)
	}
}
