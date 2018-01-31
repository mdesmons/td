package com.gresham.td.model

import java.time.*
import java.util.*
import javax.persistence.*
import com.fasterxml.jackson.annotation.JsonValue


enum class TransferType {
	unknown,
	interest,
	haircut,
	wht,
	principal,
	principalReturn;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}

enum class TransferStatus {
	active,
	cancelled;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}


@Entity
class Transfer(
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,


		var type: TransferType = TransferType.unknown,
		var currency: String = "AUD",
		var amount: Double = 0.0,
		var date: Date = Date(0),
		var narrative: String = "",
		var status: TransferStatus = TransferStatus.active,

		@ManyToOne(fetch = FetchType.EAGER)
		@JoinColumn(name = "term_deposit_id")
		var termDeposit: TermDeposit = TermDeposit()
)
