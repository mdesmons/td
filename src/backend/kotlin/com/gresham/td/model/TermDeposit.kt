package com.gresham.td.model

import java.util.*
import javax.persistence.*
import com.fasterxml.jackson.annotation.JsonValue


/*
enum class PlayerGender {
	any,
	female,
	male,
	other;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}
*/

enum class TermDepositStatus {
	opened,
	pendingClosed,
	closed;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}

enum class TermDepositPaymentType {
	atMaturity,
	monthly;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}

@Entity
class TermDeposit(
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,

		// a TD belongs to a Customer
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "location_code")
		var customer: Customer = Customer(),

		var paymentType: TermDepositPaymentType = TermDepositPaymentType.atMaturity,
		var account: String = "",
		var currency: String = "AUD",
		var sourceAccount: String = "",
		var principal: Double = 0.0,
		var interest: Double = 0.0,
		var haircut: Double = 0.0,
		var term: Int = 0,
		var openingDate: Date = Date(),
		var closingDate: Date = Date(0),
		var valueDate: Date = Date(0),
		var maturityDate: Date = Date(0),
		var status: TermDepositStatus = TermDepositStatus.opened,
		var dailyGrossCustomerInterest: Double = 0.0,
		var dailyHaircut: Double = 0.0,
		var dailyGrossClientInterest: Double = 0.0,
		var dailyWHT: Double = 0.0,
		var dailyNetClientInterest: Double = 0.0,

		// a TD has several transfers
		@OneToMany(mappedBy = "termDeposit", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
		var transfers: MutableList<Transfer> = mutableListOf<Transfer>()
)
