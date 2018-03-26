package com.gresham.td.persistence

import com.gresham.td.model.WHT

interface IWHTRepository {
	fun findWHTByAccount(clientAccountId: String): Double
}
