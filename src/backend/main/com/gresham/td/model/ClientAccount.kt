package com.gresham.td.model

import com.gresham.td.api.dto.AccountBalanceResponse
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "CLILST")
class ClientAccount() {
	@Id
	@Column(name="CLIREF")
	var id: String = ""

	@Column(name="CLINAM")
	var name: String = ""

	@Column(name="CLITYP")
	var type: String = ""

	@Column(name="CLICRC")
	var currency: String = ""

	@Transient
	var bankBalance: Double = 0.0
	@Transient
	var bankBalanceDate: Date = Date()
	@Transient
	var forwardBalance: Double = 0.0
	@Transient
	var forwardBalanceDate: Date = Date()
	@Transient
	var ledgerBalance: Double = 0.0
	@Transient
	var ledgerBalanceDate: Date = Date()

	fun setBalances(balance: AccountBalanceResponse) {
		bankBalance= balance.bankBalance
		bankBalanceDate= balance.bankBalanceDate
		forwardBalance= balance.forwardBalance
		forwardBalanceDate= balance.forwardBalanceDate
		ledgerBalance= balance.ledgerBalance
		ledgerBalanceDate= balance.ledgerBalanceDate
	}
}
