package edu.uaux.pheart.statistics

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Formats the labels of an axis to show a time of day. Interprets the float value as a whole hour of the day.
 */
class TimeAxisFormatter(private val timeFormatter: DateTimeFormatter) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        val dateTime = LocalDateTime.of(2000, 1, 1, value.toInt(), 0, 0)
        return dateTime.format(timeFormatter)
    }
}

/**
 * Removes bar labels.
 */
class NoBarLabelsFormatter : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        return ""
    }
}