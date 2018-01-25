package com.gresham.td.model

import java.time.*
import java.util.*
import javax.persistence.*
import com.fasterxml.jackson.annotation.JsonValue


enum class UserCategory {
	customer,
	desk,
	managedServices;

	@JsonValue
	fun toValue(): Int {
		return ordinal
	}
}

@Entity
class ApplicationUser(
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,

		var type: UserCategory = UserCategory.customer,
		var username: String = "",
		var password: String = "",
		var locationCodes: String = "",
		var lastLogin: Date = Date(0)
)
