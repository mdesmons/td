package com.gresham.td.persistence

import com.gresham.td.model.Quote
import com.gresham.td.model.QuoteStatus
import com.gresham.td.model.TermDeposit
import com.gresham.td.model.TermDepositStatus
import org.springframework.data.repository.CrudRepository

interface QuoteRepository : CrudRepository<Quote, Long> {
	fun findByStatus(status: QuoteStatus) : Iterable<Quote>
}
