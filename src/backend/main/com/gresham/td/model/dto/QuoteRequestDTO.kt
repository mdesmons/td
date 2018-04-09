package com.gresham.td.model.dto

import com.gresham.td.model.Quote
import java.util.*

class QuoteRequestDTO(
		val rate: Double = 0.0
) {
	fun toQuote() : Quote {
		val quote = Quote(
				rate = this.rate
		)
		return quote
	}
}
