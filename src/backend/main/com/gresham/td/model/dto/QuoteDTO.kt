package com.gresham.td.model.dto

import com.gresham.td.model.Quote

class QuoteDTO(quote: Quote) {
	val id = quote.id
	val reference = quote.reference
	val rate = quote.rate
	val openingDate = quote.openingDate
	val closingDate = quote.closingDate
	val status = quote.status
}
