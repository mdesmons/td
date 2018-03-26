package com.gresham.td.test

import com.gresham.td.TermDepositServer
import com.gresham.td.api.VBTAPIClient
import com.gresham.td.api.CustomerCryptoManager
import com.gresham.td.persistence.ClientAccountRepository
import com.gresham.td.persistence.WHTRepository
import com.gresham.td.service.WHTService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.context.junit4.SpringRunner


/*
@Configuration
class Config {

	@Bean
	fun propertiesResolver(): PropertySourcesPlaceholderConfigurer {
		return PropertySourcesPlaceholderConfigurer()
	}

}
*/

@SpringBootTest(classes = arrayOf(TermDepositServer::class))
@RunWith(SpringRunner::class)
class WHTTest {

	@Autowired
	private lateinit var whtRepository: WHTRepository

	@Test
	fun simpleTest() {
		val service = WHTService()
		service.whtRepository = whtRepository

		val rate = service.getRate("123456789")
	}

}
