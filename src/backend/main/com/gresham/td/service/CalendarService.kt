package com.gresham.td.service

import com.gresham.td.model.TermDepositPaymentType
import org.apache.commons.csv.CSVFormat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors

/**
 * Utility service that implements various class functions
 */
@Service
@Configuration
class CalendarService : InitializingBean {
	private val logger = LoggerFactory.getLogger(CalendarService::class.java)

	/**
	 * Folder that contains the calendar file
	 */
	@Value("\${calendar-file.folder}")
	private val calendarFileFolder: String? = null

	/**
	 * List of public holidays, loaded from the calendar file
	 */
	private val holidays : MutableList<Date> = mutableListOf<Date>()

	/**
	 * Reads the initial calendar file
	 */
	override fun afterPropertiesSet() {
		loadCalendarFile()
	}

	/**
	 * Read the calendar file at scheduled intervals.
	 *
	 * The calendar file contains dates in yyyy-MM-dd format (one date per line)
	 *
	 * Lines starting with # or empty lines are ignored
	 */
	@Scheduled(cron = "\${calendar-file.schedule}")
	private fun loadCalendarFile() {
		logger.info("Loading calendar file")
		holidays.clear()
		val calendarFile = File(calendarFileFolder + File.separator + "calendar.csv")
		val sdf = SimpleDateFormat("yyyy-MM-dd")

		if (Files.isRegularFile(calendarFile.toPath())) {
			val reader = BufferedReader(FileReader(calendarFile))
			holidays.addAll(0, reader.lines()
					.filter{!it.startsWith("#")}
					.filter{it.isNotBlank()}
					.map { sdf.parse(it) }
					.collect(Collectors.toList()))
		} else {
			logger.warn("Unable to read calendar file")
		}
	}


	/**
	 * Checks if a given date is a business day. A business day is any day between Monday and Friday
	 * @param cal the date to check
	 * @return true if [date] is a business day, false otherwise
	 */
	private fun isBusinessDay(cal: Calendar) : Boolean{
		val day = cal.get(Calendar.DAY_OF_WEEK)

		/* Return true if the date is not a Sunday, not a Saturday and not in the list of holidays */
		return (day != Calendar.SATURDAY &&
				day != Calendar.SUNDAY &&
				holidays.contains(cal.time))
	}


	/**
	 * Checks if a given date is a business day. A business day is any day between Monday and Friday that is not a public holiday
	 * @param date the date to check
	 * @return true if [date] is a business day, false otherwise
	 */
	fun isBusinessDay(date: Date) : Boolean{
		val cal = Calendar.getInstance()
		cal.time = date
		return isBusinessDay(cal)
	}

	/**
	 * Returns the next business day for a given date
	 * @param date the date for which we want to find the next business day
	 * @return the provided date's following business day
	 */
	fun nextBusinessDay(date: Date) : Date {
		val cal = Calendar.getInstance()
		cal.time = date
		cal.add(Calendar.DAY_OF_MONTH, 1)
		while(!isBusinessDay(cal)) {
			cal.add(Calendar.DAY_OF_MONTH, 1)
		}
		return cal.time
	}
	
	/**
	 * Add a time period to a date
	 * @param date the date to add the period to
	 * @param unit the period unit (eg Calendar.MONTH, Calendar.DAY_OF_MONTH ...).
	 * @param number the number of periods to add
	 * @return the resulting date
	 */
	fun addPeriod(date: Date, unit: Int, number: Int) : Date {
		val cal = Calendar.getInstance()
		cal.time = date
		cal.add(unit, number)
		return cal.time
	}


	/**
	 * Calculate the number of days between 2 dates
	 * @param dateTo the end of the date range
	 * @param dateFrom the start of the date range
	 * @return the number of days between [dateTo] and [dateFrom]
	 */
	fun diffDays(dateTo: Date, dateFrom: Date): Long {
		val d1 = dateTo.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate()
		val d2 = dateFrom.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDate()

		return ChronoUnit.DAYS.between(d2, d1)
	}
}
