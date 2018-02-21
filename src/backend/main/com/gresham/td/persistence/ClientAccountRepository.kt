package com.gresham.td.persistence

import com.gresham.td.model.ClientAccount
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface ClientAccountRepository : CrudRepository<ClientAccount, Long> {
	@Query("SELECT c FROM ClientAccount c WHERE SUBSTRING(c.id, 1, 6) = :locationCode AND c.type = :type")
	fun clientAccounts(@Param("locationCode") locationCode: String, @Param("type") type: String): Iterable<ClientAccount>
	fun findById(id: String): ClientAccount?

//	fun allAccounts(): List<ClientAccount>
	//fun findByLocationCode(code: String): Customer

}
