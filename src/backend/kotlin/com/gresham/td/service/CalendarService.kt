package com.gresham.td.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.ZoneId
import java.util.*
import java.time.temporal.ChronoUnit

@Service
class CalendarService {
	private val logger = LoggerFactory.getLogger(CalendarService::class.java)

	fun isBusinessDay(date: Date) : Boolean{
		val cal = Calendar.getInstance()
		cal.time = date
		val day = cal.get(Calendar.DAY_OF_WEEK)
		return (day == Calendar.SATURDAY || day == Calendar.SUNDAY)
	}

	fun nextBusinessDay(date: Date) : Date {
		val cal = Calendar.getInstance()
		cal.time = date
		cal.add(Calendar.DAY_OF_MONTH, 1)
		while(cal.get(Calendar.DAY_OF_WEEK) in listOf(Calendar.SATURDAY, Calendar.SUNDAY)) {
			cal.add(Calendar.DAY_OF_MONTH, 1)
		}
		return cal.time
	}

	fun addDays(date: Date, number: Int) : Date {
		val cal = Calendar.getInstance()
		cal.time = date
		cal.add(Calendar.DAY_OF_MONTH, number)
		return cal.time
	}

	fun addMonths(date: Date, number: Int) : Date {
		val cal = Calendar.getInstance()
		cal.time = date
		cal.add(Calendar.MONTH, number)
		return cal.time
	}

	fun diffDays(dateTo: Date, dateFrom: Date): Int {
		val d1 = dateTo.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate()
		val d2 = dateFrom.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate()

		return ChronoUnit.DAYS.between(d2, d1).toInt()
	}
}
