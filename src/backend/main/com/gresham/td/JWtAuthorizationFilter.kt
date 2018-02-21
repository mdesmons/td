package com.gresham.td

import com.gresham.td.SecurityConstants.HEADER_STRING
import com.gresham.td.SecurityConstants.SECRET
import com.gresham.td.SecurityConstants.TOKEN_PREFIX
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import javax.servlet.ServletException
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import java.text.SimpleDateFormat
import java.util.*
import javax.servlet.http.HttpServletRequest


class JWTAuthorizationFilter(authManager: AuthenticationManager) : BasicAuthenticationFilter(authManager) {

	@Throws(IOException::class, ServletException::class)
	protected override fun doFilterInternal(req: HttpServletRequest,
								   res: HttpServletResponse,
								   chain: FilterChain) {
		val header = req.getHeader(HEADER_STRING)

		if (header == null || !header.startsWith(TOKEN_PREFIX)) {
			chain.doFilter(req, res)
			return
		} else {
				val authentication = getAuthentication(req)

				SecurityContextHolder.getContext().authentication = authentication
				chain.doFilter(req, res)
		}
	}

	private fun getAuthentication(request: HttpServletRequest): UsernamePasswordAuthenticationToken? {
		val token = request.getHeader(HEADER_STRING)
		if (token != null) {
			val verifier = MACVerifier(SECRET)
			val parsedToken = SignedJWT.parse(token.replace(TOKEN_PREFIX, ""))
			if (!parsedToken.verify(verifier)) {
				logger.error("JWT signature verification failed")
				return null;
			}

			val claimsSet = parsedToken.jwtClaimsSet
			val now = Date()
			if (claimsSet.expirationTime.before(now)) {
				val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
				logger.error("JWT expired. Expiration time: " + sdf.format(claimsSet.expirationTime))
				return null
			}

			val user = claimsSet.subject
			val scope = claimsSet.getStringClaim("scope")
			return if (user != null) {
				UsernamePasswordAuthenticationToken(user, null, mutableListOf<GrantedAuthority>(SimpleGrantedAuthority(scope)))
			} else null
		} else {
			logger.error("Request does not contain any " + HEADER_STRING + " header")
		}
		return null
	}
}
