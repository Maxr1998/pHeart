package edu.uaux.pheart.statistics

import java.time.ZonedDateTime

object StatisticsUtils {

    fun createDayRange(zonedDateTime: ZonedDateTime): ZonedDateTimeRange {
        val start = zonedDateTime.run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
        }
        val end = zonedDateTime.run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59, 999, zone)
        }
        return ZonedDateTimeRange(start, end)
    }

    fun createLast7DaysRange(zonedDateTime: ZonedDateTime): ZonedDateTimeRange {
        val start = zonedDateTime.minusDays(6).run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
        }
        val end = zonedDateTime.run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59, 999, zone)
        }
        return ZonedDateTimeRange(start, end)
    }
}