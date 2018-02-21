package com.gresham.td.persistence

import com.gresham.td.model.ApplicationUser
import org.springframework.data.repository.CrudRepository

interface ApplicationUserRepository : CrudRepository<ApplicationUser, Long> {
	fun findByUsername(username: String): ApplicationUser?
}
