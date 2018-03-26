package com.gresham.td.persistence

import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import javax.persistence.EntityManager
import javax.persistence.ParameterMode
import javax.persistence.PersistenceContext

@Transactional
@Repository
class WHTRepository : IWHTRepository {
	@PersistenceContext
	lateinit var entityManager: EntityManager

	/**
	 * Gets the WHT rate of a client account by invoking the VBT WHT calculation stored proc
	 * @param clientAccountId the client account holder reference
	 * @return the client account WHT rate
	 * @throws IllegalArgumentException if the [clientAccountId] provided has an incorrect format
	 */
	override fun findWHTByAccount(clientAccountId: String) : Double {
		val query = entityManager.createStoredProcedureQuery("WT_AustralianCalculation")
		clientAccountId.length > 6 || throw IllegalArgumentException("Invalid client account id " + clientAccountId)

		// SortCode
		query.registerStoredProcedureParameter(1, String::class.java, ParameterMode.IN)

		// AccountNumber
		query.registerStoredProcedureParameter(2, String::class.java, ParameterMode.IN)

		// Gross
		query.registerStoredProcedureParameter(3, BigDecimal::class.java, ParameterMode.IN)

		// nWithholdingTax (??)
		query.registerStoredProcedureParameter(4, BigDecimal::class.java, ParameterMode.IN)

		// Tax Calculation
		query.registerStoredProcedureParameter(5, BigDecimal::class.java, ParameterMode.INOUT)

		//Pass the parameter values
		val result = BigDecimal.valueOf(0.0)
		query.setParameter(1, clientAccountId.substring(0, 6))
		query.setParameter(2, clientAccountId.substring(6))
		query.setParameter(3, BigDecimal.valueOf(100.00))
		query.setParameter(4, BigDecimal.valueOf(0.00))
		query.setParameter(5, result)

		//Execute query
		query.execute()

		//Get WHT amount for an initial amount of $100.00
		val outCode = query?.getOutputParameterValue(5) as BigDecimal
		return outCode.toDouble()
	}
}
