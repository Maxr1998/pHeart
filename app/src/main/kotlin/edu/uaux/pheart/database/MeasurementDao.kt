package edu.uaux.pheart.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MeasurementDao {

    @Query("select * from user")
    fun getAll(): LiveData<List<Measurement>>

    /**
     * Note: start and end inclusive
     */
    @Query("select * from user where timestamp between (:start) and (:end)")
    fun getAll(start: Long, end: Long): LiveData<List<Measurement>>

    @Insert
    fun insert(measurement: Measurement)

    @Insert
    fun insertAll(measurements: List<Measurement>)

    @Delete
    fun delete(measurement: Measurement)

    @Query("delete from user")
    fun deleteAll()
}