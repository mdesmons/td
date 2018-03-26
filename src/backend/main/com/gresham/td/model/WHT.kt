package com.gresham.td.model

import javax.persistence.*
import kotlin.jvm.Transient

@Entity
class WHT(
		@Id @GeneratedValue(strategy = GenerationType.AUTO)
		var id: Long = 0,

		@Transient
		var rate: Double = 0.0
)

