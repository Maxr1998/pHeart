package edu.uaux.pheart.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "user")
data class Measurement(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "bpm") val bpm: Int,
    @ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel,
) {
    val dateInstant: Instant
        get() = Instant.ofEpochSecond(timestamp)
}