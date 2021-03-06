package com.gresham.td.controller

import com.gresham.td.model.dto.CloseTermDepositRequestDTO
import com.gresham.td.model.dto.TermDepositDTO
import com.gresham.td.service.TermDepositService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/td")
class TermDepositControllerV1() {
	@Autowired
	lateinit var termDepositService: TermDepositService

	// close a TD
	@DeleteMapping("/{id}/")
	fun closeTermDeposit(principal: Principal?, @PathVariable id: Long, @RequestBody request: CloseTermDepositRequestDTO): Map<String, List<TermDepositDTO>> {
		if (principal != null) {
			return hashMapOf("termDeposits" to listOf(termDepositService.closeTermDeposit(principal.name, id, request)))
		}
		throw IllegalArgumentException("Unknown user")
	}

	// close all Pending CLose TDs whose close date is today
	@PostMapping("/closePending/")
	fun closePendingTermDeposits(principal: Principal?): Map<String, List<TermDepositDTO>> {
		if (principal != null) {
			return hashMapOf("termDeposits" to termDepositService.closePendingTermDeposits(principal.name))
		}
		throw IllegalArgumentException("Unknown user")
	}

	// mature all TDs whose time has come
	@PostMapping("/mature/")
	fun matureTermDeposits(principal: Principal?): Map<String, List<TermDepositDTO>> {
		if (principal != null) {
			return hashMapOf("termDeposits" to termDepositService.matureTermDeposits(principal.name))
		}
		throw IllegalArgumentException("Unknown user")
	}

	@PostMapping("/notifycache/")
	fun notifyCache(principal: Principal?): Map<String, Map<String, Double>> {
		if (principal != null) {
			return hashMapOf("accruedInterest" to termDepositService.notifyCache(principal.name))
		}
		throw IllegalArgumentException("Unknown user")
	}


}
