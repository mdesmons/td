package com.gresham.td.service

import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import com.gresham.td.persistence.ApplicationUserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service


@Service
class UserDetailsServiceImpl(private val applicationUserRepository: ApplicationUserRepository) : UserDetailsService {

	@Throws(UsernameNotFoundException::class)
	override fun loadUserByUsername(username: String): UserDetails {
		val applicationUser = applicationUserRepository.findByUsername(username) ?: throw UsernameNotFoundException(username)
		return User(applicationUser.username, applicationUser.password, mutableListOf<GrantedAuthority>(SimpleGrantedAuthority(applicationUser.type.toString())))
	}
}
