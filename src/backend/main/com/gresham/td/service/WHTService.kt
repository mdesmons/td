package com.gresham.td.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

// this service calculates WHT rates
@Service
class WHTService() {
	private val logger = LoggerFactory.getLogger(WHTService::class.java)

	//TODO add Client Account as parameter
	fun getRate(): Double {
		return 10.00
	}
}
