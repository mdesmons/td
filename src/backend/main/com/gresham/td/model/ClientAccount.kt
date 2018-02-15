package com.gresham.td.model

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
}
