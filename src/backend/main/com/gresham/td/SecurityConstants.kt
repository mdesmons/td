package com.gresham.td

object SecurityConstants {
	val SECRET = "SecretKeyToGenJWTs"
	val EXPIRATION_TIME: Long = 864000000 // 10 days
	val TOKEN_PREFIX = "Bearer "
	val HEADER_STRING = "Authorization"
	val HEADER_STRING_REFRESH_TOKEN = "Refresh"
	val SIGN_UP_URL = "/users/init"
	val SYSTEMACCOUNT = "maXDOglj5vZW"
}
