package com.gresham.td.persistence

import com.gresham.td.model.Customer
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Long> {
	fun findByLocationCode(code: String): Customer

}
