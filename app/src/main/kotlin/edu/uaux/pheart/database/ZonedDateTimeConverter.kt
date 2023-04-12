package edu.uaux.pheart.database

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Converts ZonedDateTime to a iso datetime string and vice versa to be saved to the database.
 */
class ZonedDateTimeConverter {
    @TypeConverter
    fun fromString(value: String?): ZonedDateTime? {
        return value?.let { ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME) }
    }

    @TypeConverter
    fun toString(dateTime: ZonedDateTime?): String? {
        return dateTime?.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }
}