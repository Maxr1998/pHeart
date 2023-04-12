package edu.uaux.pheart.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
@Entity(tableName = "measurement")
data class Measurement(
    @PrimaryKey val timestamp: ZonedDateTime,
    @ColumnInfo(name = "bpm") val bpm: Int,
    @ColumnInfo(name = "activity_level") val activityLevel: ActivityLevel,
) : Parcelable