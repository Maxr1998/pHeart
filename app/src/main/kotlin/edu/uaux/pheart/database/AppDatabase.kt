package edu.uaux.pheart.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Measurement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun measurementDao(): MeasurementDao
}