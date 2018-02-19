package com.gresham.td.test

import com.gresham.td.api.VBTAPIClient
import com.gresham.td.api.CustomerCryptoManager
import org.junit.Test
import org.junit.runner.RunWith
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

@SpringBootTest(classes = arrayOf(VBTAPIClientTest::class))
@RunWith(SpringRunner::class)
class VBTAPIClientTest {

	val certRoot: String = "C:/Users/mdesmons/Dev/td/src/backend/test/resources"
	@Test
	fun simpleTest() {


		val keyManager = CustomerCryptoManager("gresham.p12", "gresham", "gresham")
		ReflectionTestUtils.setField(keyManager, "certRoot", certRoot)

		val apiClient = VBTAPIClient()
		apiClient.accountBalance(accountHolderRef = "FOO")
	}

}
