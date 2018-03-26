package com.gresham.td.service

import com.gresham.td.persistence.IWHTRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * this service calculates WHT rates
 */
@Service
class WHTService {
	private val logger = LoggerFactory.getLogger(WHTService::class.java)

	@Autowired
	lateinit var whtRepository: IWHTRepository

	/**
	 * Gets the WHT rate of a client account
	 * @param clientAccountId the client account holder reference
	 * @return the WHT rate (usually 0, 10 or 45)
	 */
	fun getRate(clientAccountId: String): Double {
		return whtRepository.findWHTByAccount(clientAccountId)
	}
}
