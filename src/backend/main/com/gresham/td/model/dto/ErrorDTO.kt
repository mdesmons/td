package com.gresham.td.model.dto

class ErrorDTO(val message:String = "Unknown error", val id:Long = 0) {
	constructor (e: Exception) : this(message = e.message?:"Unknown error")
}
