package com.gresham.td.controller

import com.gresham.td.model.dto.QuoteDTO
import com.gresham.td.model.dto.QuoteRequestDTO
import com.gresham.td.service.QuoteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/quote")
class QuoteControllerV1() {
	@Autowired
	lateinit var quoteService: QuoteService

	// close a quote
	@DeleteMapping("/{id}/")
	fun closeQuote(principal: Principal?, @PathVariable id: Long): Map<String, List<QuoteDTO>> {
		if (principal != null) {
			return hashMapOf("quotes" to listOf(quoteService.closeQuote(principal.name, id)))
		}
		throw IllegalArgumentException("Unknown user")
	}

	// create a quote for a customer
	@PostMapping("/{location_code}/")
	fun addQuote(principal: Principal?, @PathVariable location_code: String, @RequestBody request: QuoteRequestDTO) : Map<String, List<QuoteDTO>> {
		if (principal != null) {
			return hashMapOf("quotes" to listOf(quoteService.addQuote(principal.name, location_code, request)))
		}
		throw IllegalArgumentException("Unknown user")
	}
}
