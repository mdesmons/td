package com.gresham.td.controller

import com.gresham.td.service.TermDepositService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/td")
class TermDepositControllerV1() {
	@Autowired
	lateinit var termDepositService: TermDepositService


}
