package edu.uaux.pheart.database

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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