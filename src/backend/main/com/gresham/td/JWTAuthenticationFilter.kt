package com.gresham.td

import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import com.gresham.td.model.ApplicationUser
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.http.HttpServletRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.core.userdetails.User
import java.util.*

import com.gresham.td.SecurityConstants.EXPIRATION_TIME
import com.gresham.td.SecurityConstants.HEADER_STRING
import com.gresham.td.SecurityConstants.SECRET
import com.gresham.td.SecurityConstants.TOKEN_PREFIX
import io.jsonwebtoken.Claims
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority

class JWTAuthenticationFilter(private val authManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

	@Throws(AuthenticationException::class)
	override fun attemptAuthentication(req: HttpServletRequest,
							  res: HttpServletResponse): Authentication {
		try {
			val creds = ObjectMapper()
					.readValue(req.inputStream, ApplicationUser::class.java)

			return authManager.authenticate(
					UsernamePasswordAuthenticationToken(
							creds.username,
							creds.password,
							mutableListOf<GrantedAuthority>(SimpleGrantedAuthority(creds.type.toString()))))
		} catch (e: IOException) {
			throw RuntimeException(e)
		}

	}

	@Throws(IOException::class, ServletException::class)
	override fun successfulAuthentication(req: HttpServletRequest,
										   res: HttpServletResponse,
										   chain: FilterChain,
										   auth: Authentication) {
		val username = (auth.principal as User).username
		val scopes = (auth.principal as User).authorities.joinToString { it.authority }
		val token = Jwts.builder()
				.setClaims(hashMapOf<String, Any>("scope" to scopes))
				.setSubject(username)
				.setExpiration(Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET.toByteArray())
				.compact()
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + token)
	}
}
