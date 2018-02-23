package com.gresham.td.model.dto

import com.gresham.td.model.TermDepositPaymentType


class RateRequestDTO(
	val currency: String = "AUD",
	val principal: Double = 0.0,
	val term:Int = 0, // term in days
	val maturity: Long = 0, // in milliseconds since the epoch. Can be used to specify a maturity date, instead of a term
	val paymentType: TermDepositPaymentType = TermDepositPaymentType.AtMaturity
)
