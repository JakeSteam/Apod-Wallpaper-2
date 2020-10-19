package uk.co.jakelee.apodwallpaper.app

import java.text.SimpleDateFormat
import java.util.*

class ApodDateParser {

    data class ApodDateRange(val startDate: String, val endDate: String)

    private val apodDateFormat = SimpleDateFormat("yyyy-MM-dd")

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

    fun getPreviousDate(date: String) {

    }

     fun getNextDate(date: String) {

     }
}