package com.gresham.td.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class HomeController() {
	@RequestMapping(value = "/")
	fun index(): String = "index"

	@RequestMapping(value = "/subscribe/*")
	fun subscribe(): String = "index"

	@RequestMapping(value = "/callback")
	fun callback(): String = "index"

	@RequestMapping(value = "/profile")
	fun profile(): String = "index"
}
