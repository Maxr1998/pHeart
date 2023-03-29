package edu.uaux.pheart.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.ZonedDateTime

@Dao
interface MeasurementDao {

    @Query("select * from user order by timestamp desc")
    fun getAll(): LiveData<List<Measurement>>

    /**
     * Note: start and end inclusive
     */
    @Query("select * from user where timestamp between (:start) and (:end) order by timestamp desc")
    suspend fun getAll(start: ZonedDateTime, end: ZonedDateTime): List<Measurement>

    @Insert
    fun insert(measurement: Measurement)

    @Insert
    fun insertAll(measurements: List<Measurement>)

    @Delete
    fun delete(measurement: Measurement)

    @Query("delete from user")
    fun deleteAll()
}