package com.gresham.td.api.dto

class AccountBalanceRequest(val accountHolderRef: String, val currency: String = "AUD", val accountType: String = "CLI") {

}
