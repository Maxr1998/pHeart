package edu.uaux.pheart.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZoneId
import java.time.ZonedDateTime

@Entity(tableName = "user")
data class Measurement(
    @PrimaryKey val timestamp: ZonedDateTime,
    @ColumnInfo(name = "bpm") val bpm: Int,
    @ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel,
) {
    companion object {
        fun createNow(bpm: Int, activityLevel: ActivityLevel): Measurement {
            return Measurement(ZonedDateTime.now(ZoneId.systemDefault()), bpm, activityLevel)
        }
    }
}