package com.gresham.td

import com.gresham.td.SecurityConstants.SIGN_UP_URL
import com.gresham.td.model.UserCategory
import com.gresham.td.persistence.CustomerRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.http.HttpMethod
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*
import org.springframework.web.filter.ShallowEtagHeaderFilter
import javax.servlet.Filter


@Configuration
@EnableWebSecurity
open class WebSecurity(val userDetailsService: UserDetailsService, val bCryptPasswordEncoder: BCryptPasswordEncoder) : WebSecurityConfigurerAdapter() {
	@Autowired
	lateinit var authManager: AuthenticationManager

	@Value("\${jwt_expiration_time_in_seconds}")
	private var expirationTime: Int = 0

	@Throws(Exception::class)
	override fun configure(http: HttpSecurity) {


				http.cors().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
	//			.antMatchers("/resources/**", "/").permitAll()
				.antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
						.antMatchers("/api/admin**").hasAuthority(UserCategory.desk.toString())
						.antMatchers("/api/**").hasAnyAuthority(UserCategory.customer.toString(), UserCategory.desk.toString())
						.anyRequest().authenticated()
						.and()
						.addFilter(JWTAuthenticationFilter(authenticationManager(), expirationTime))
						.addFilter(JWTAuthorizationFilter(authenticationManager()))
						.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						.and()
//
//				antMatchers("/**").permitAll()
		http.headers().cacheControl().disable();

	}

	override fun configure(auth: AuthenticationManagerBuilder?) {
		auth?.userDetailsService(userDetailsService)?.passwordEncoder(bCryptPasswordEncoder)
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration()
		configuration.allowedOrigins = Arrays.asList("http://localhost:8080")
		configuration.allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE")
		configuration.allowedHeaders = listOf<String>("*")
		configuration.addExposedHeader("ETag")
		configuration.addExposedHeader("Authorization")
		val source = UrlBasedCorsConfigurationSource()
		source.registerCorsConfiguration("/api/**", configuration)
		source.registerCorsConfiguration("/login", configuration)
		return source
	}

	@Bean
	fun shallowETagHeaderFilter(): Filter {
		return ShallowEtagHeaderFilter()
	}
}
