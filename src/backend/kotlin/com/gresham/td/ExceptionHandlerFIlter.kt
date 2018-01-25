package com.gresham.td

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.gresham.td.model.dto.ErrorDTO
import org.apache.http.HttpStatus
import java.io.IOException
import javax.servlet.ServletException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.http.HttpServletRequest


class ExceptionHandlerFilter : OncePerRequestFilter() {

	@Throws(ServletException::class, IOException::class)
	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		try {
			filterChain.doFilter(request, response)
		} catch (e: RuntimeException) {

			val errorResponse = ErrorDTO(e)
			response.status = HttpStatus.SC_INTERNAL_SERVER_ERROR
			response.setHeader("Content-Type", "application/json")
			response.writer.write(convertObjectToJson(errorResponse)!!)
		}

	}

	@Throws(JsonProcessingException::class)
	fun convertObjectToJson(obj: Any?): String? {
		if (obj == null) {
			return null
		}
		val mapper = ObjectMapper()
		return mapper.writeValueAsString(obj)
	}
}
