package uk.co.jakelee.apodwallpaper.app

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ApodDateParser {

    data class ApodDateRange(val startDate: String, val endDate: String)

    private val apodDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    fun pageToDateRange(page: Int): ApodDateRange {
        val targetDate = Calendar.getInstance()
        targetDate.add(Calendar.MONTH, -page)
        if (page != 0) {
            targetDate.set(Calendar.DAY_OF_MONTH, targetDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        }
        val endDate = apodDateFormat.format(targetDate.time)

        targetDate.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = apodDateFormat.format(targetDate.time)

        return ApodDateRange(startDate, endDate)
    }

    fun getPreviousDate(date: String): String? {
        apodDateToCalendar(date)?.let {
            it.add(Calendar.DAY_OF_YEAR, -1)
            return calendarToApodDate(it)
        }
        return null
    }

    fun getNextDate(date: String): String? {
        apodDateToCalendar(date)?.let {
            it.add(Calendar.DAY_OF_YEAR, 1)
            if (isPreviousDate(it)) {
                return calendarToApodDate(it)
            }
        }
        return null
    }

    private fun isPreviousDate(calendar: Calendar): Boolean {
        val now = Calendar.getInstance()
        val isPreviousYear = calendar.get(Calendar.YEAR) < now.get(Calendar.YEAR)
        val isPreviousDayThisYear = calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) <= now.get(Calendar.DAY_OF_YEAR)
        return isPreviousYear || isPreviousDayThisYear
    }

    fun currentApodDate(): String = calendarToApodDate(Calendar.getInstance())

    fun apodDateToCalendar(apodDate: String): Calendar? {
        return try {
            val calendar = Calendar.getInstance()
            apodDateFormat.parse(apodDate)?.let {
                calendar.time = it
                calendar.add(Calendar.HOUR, 12)
                return calendar
            }
            null
        } catch (e: ParseException) {
            null
        }
    }

    fun calendarToApodDate(calendar: Calendar) = apodDateFormat.format(calendar.time)
}