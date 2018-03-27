package com.gresham.td.model

import java.util.*
import javax.persistence.*
import com.fasterxml.jackson.annotation.JsonValue

enum class TermDepositStatus {
	Opened,
	PendingClosed,
	Closed;

	@JsonValue
	fun toValue() = ordinal
}

enum class TermDepositCloseReason {
	None,
	MaturityReached,
	NoticePeriod,
	FinancialHardship,
	System;


	@JsonValue
	fun toValue() = ordinal
}

enum class TermDepositPaymentType {
	AtMaturity,
	Monthly,
	Quarterly,
	HalfYearly,
	Yearly;

	@JsonValue
	fun toValue() = ordinal
}

@Entity
class TermDeposit(
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,

		// a TD belongs to a Customer
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "location_code")
		var customer: Customer = Customer(),

		var paymentType: TermDepositPaymentType = TermDepositPaymentType.AtMaturity,
		var account: String = "",
		var currency: String = "AUD",
		var sourceAccount: String = "",
		var principal: Double = 0.0,
		var interest: Double = 0.0,
		var haircut: Double = 0.0,
		var term: Int = 0,
		var openingDate: Date = Date(),
		var valueDate: Date = Date(0),   // date the TD starts accrue interest
		var maturityDate: Date = Date(0),
		var status: TermDepositStatus = TermDepositStatus.Opened,
		var dailyGrossCustomerInterest: Double = 0.0,
		var dailyHaircut: Double = 0.0,
		var dailyGrossClientInterest: Double = 0.0,
		var dailyWHT: Double = 0.0,
		var dailyNetClientInterest: Double = 0.0,
		var reasonForClose: TermDepositCloseReason = TermDepositCloseReason.None,
		var closingDate: Date = Date(0),  // date at which the principal is returned  (could be a WE)
		var technicalClosingDate: Date = Date(0), /* date at which we instruct VBT to close the account.
		This is when the payment of the Principal gets returned in the BTR */


		// a TD has several transfers attached to it
		@OneToMany(mappedBy = "termDeposit", cascade = arrayOf(CascadeType.ALL), fetch = FetchType.EAGER)
		var transfers: MutableList<Transfer> = mutableListOf()
)
