package edu.uaux.pheart.statistics

import java.time.ZonedDateTime

object StatisticsUtils {
    fun createDayRange(zonedDateTime: ZonedDateTime) =
        ZonedDateTimeRange(zonedDateTime.startOfDay(), zonedDateTime.endOfDay())

    fun createLast7DaysRange(zonedDateTime: ZonedDateTime) =
        ZonedDateTimeRange(zonedDateTime.minusDays(6).startOfDay(), zonedDateTime.endOfDay())
}

fun ZonedDateTime.startOfDay() = ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
fun ZonedDateTime.endOfDay() = ZonedDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59, 999, zone)