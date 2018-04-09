package com.gresham.td.model

import java.util.*
import javax.persistence.*
import com.fasterxml.jackson.annotation.JsonValue

enum class QuoteStatus {
	Opened,
	Closed;

	@JsonValue
	fun toValue() = ordinal
}

@Entity
class Quote (
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,

		// a quote is linked to to a Customer
		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "location_code")
		var customer: Customer = Customer(),

		var reference: String = "",
		var rate: Double = 0.0,
		var openingDate: Date = Date(),
		var closingDate: Date = Date(0),
		var status: QuoteStatus = QuoteStatus.Opened
)
