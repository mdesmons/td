package com.gresham.td

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

class UserStatusFilter(authManager: AuthenticationManager) : FilterSecurityInterceptor() {
	
}
