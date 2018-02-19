package com.gresham.td.api.dto

import java.util.*

class AccountBalanceResponse() {
	var bankBalance: Double = 0.0
	var bankBalanceDate: Date = Date()
	var forwardBalance: Double = 0.0
	var forwardBalanceDate: Date = Date()
	var ledgerBalance: Double = 0.0
	var ledgerBalanceDate: Date = Date()
}
