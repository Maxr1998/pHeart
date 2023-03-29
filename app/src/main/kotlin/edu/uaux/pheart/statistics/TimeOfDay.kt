package edu.uaux.pheart.statistics

import java.util.Locale

/**
 * Represents a time of day without any locale or date.
 */
data class TimeOfDay(val hours: Int, val minutes: Int, val seconds: Int) {
    init {
        require(hours in 0..23)
        require(minutes in 0..59)
        require(seconds in 0..59)
    }

    fun toFormattedString(includeSeconds: Boolean = false) = if (includeSeconds) {
        String.format(Locale.getDefault(), "%02d:%02d:%02s", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
    }
}
