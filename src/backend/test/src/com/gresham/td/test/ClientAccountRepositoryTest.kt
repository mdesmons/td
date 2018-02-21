package com.gresham.td.test

import com.gresham.td.TermDepositServer
import com.gresham.td.persistence.ClientAccountRepository
import com.gresham.td.service.CustomerService
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(TermDepositServer::class))
class ClientAccountRepositoryTest {
	private val logger = LoggerFactory.getLogger(CustomerService::class.java)

	@Autowired
	private lateinit var clientAccountRepository: ClientAccountRepository

	@Test
	fun AccountRepositoryTest() {

//	clientAccountRepository.findAll().forEach { logger.info(it.reference) }
	clientAccountRepository.clientAccounts("446688", "CLI").forEach { logger.info(it.name) }
	}
}
