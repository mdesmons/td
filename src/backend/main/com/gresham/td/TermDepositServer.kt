package com.gresham.td

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@SpringBootApplication
@EnableAsync
@EnableScheduling

open class TermDepositServer {
	@Bean
	fun bCryptPasswordEncoder() : BCryptPasswordEncoder {
		return BCryptPasswordEncoder()
	}
}

fun main(args: Array<String>) {
	SpringApplication.run(TermDepositServer::class.java, *args)
}
