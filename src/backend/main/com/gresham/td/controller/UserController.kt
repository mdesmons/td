package com.gresham.td.controller

import com.gresham.td.SecurityConstants.SYSTEMACCOUNT
import org.apache.tomcat.jni.SSL.setPassword
import com.gresham.td.model.ApplicationUser
import com.gresham.td.model.UserCategory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import com.gresham.td.persistence.ApplicationUserRepository
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/users")
class UserController(private val applicationUserRepository: ApplicationUserRepository,
					 private val bCryptPasswordEncoder: BCryptPasswordEncoder) {

	@PostMapping("/init")
	fun init() {
		val user = ApplicationUser(username = "admin",
					password = bCryptPasswordEncoder.encode(SYSTEMACCOUNT),
					type = UserCategory.desk,
					locationCodes = "*")

		applicationUserRepository.save(user)
	}
}
