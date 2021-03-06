package com.gresham.td.controller

import com.gresham.td.model.dto.ClientAccountDTO
import com.gresham.td.service.CustomerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/clientaccount")
class ClientAccountControllerV1 {
	@Autowired
	lateinit var customerService: CustomerService

	// get balance & other information for a given client account
	@GetMapping("/{id}/")
	fun clientAccountDetails(principal: Principal?, @PathVariable id: String) : Map<String, List<ClientAccountDTO>> {
		if (principal != null) {
			return hashMapOf("clientAccounts" to listOf(customerService.clientAccountDetails(principal.name, id)))
		}
		throw IllegalArgumentException("Unknown user")
	}
}
