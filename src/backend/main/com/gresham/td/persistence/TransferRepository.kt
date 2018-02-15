package com.gresham.td.persistence

import com.gresham.td.model.Transfer
import org.springframework.data.repository.CrudRepository

interface TransferRepository : CrudRepository<Transfer, Long>
