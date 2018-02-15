package com.gresham.td.persistence

import com.gresham.td.model.TermDeposit
import com.gresham.td.model.TermDepositStatus
import org.springframework.data.repository.CrudRepository

interface TermDepositRepository : CrudRepository<TermDeposit, Long> {
	fun findByStatus(status: TermDepositStatus) : Iterable<TermDeposit>
}
