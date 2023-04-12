package edu.uaux.pheart.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * The database for the app.
 */
@Database(entities = [Measurement::class], version = 3)
@TypeConverters(ZonedDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}