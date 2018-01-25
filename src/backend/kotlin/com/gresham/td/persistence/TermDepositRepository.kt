package com.gresham.td.persistence

import com.gresham.td.model.TermDeposit
import org.springframework.data.repository.CrudRepository

interface TermDepositRepository : CrudRepository<TermDeposit, Long>
