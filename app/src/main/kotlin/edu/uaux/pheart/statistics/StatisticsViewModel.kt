package edu.uaux.pheart.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import edu.uaux.pheart.database.AppDatabase
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import java.time.ZoneId
import java.time.ZonedDateTime

class StatisticsViewModel(app: Application) : AndroidViewModel(app), KoinComponent {

    private val measurementDao: MeasurementDao = get<AppDatabase>().measurementDao()

    private val _selectedMeasurement: MutableLiveData<Measurement?> = MutableLiveData<Measurement?>(null)
    private val _dailyMeasurements: MutableLiveData<List<Measurement>> = MutableLiveData(emptyList())
    private val _dayInstant: MutableLiveData<ZonedDateTime?> = MutableLiveData(null)

    val selectedMeasurement: LiveData<Measurement?>
        get() = _selectedMeasurement
    val dailyMeasurements: LiveData<List<Measurement>>
        get() = _dailyMeasurements
    val dayInstant: LiveData<ZonedDateTime?>
        get() = _dayInstant

    fun loadToday() {
        loadDay(ZonedDateTime.now(ZoneId.systemDefault()))
    }

    private fun loadDay(zonedDateTime: ZonedDateTime) {
        val range = createDayRange(zonedDateTime)
        this.viewModelScope.launch {
            val dailyMeasurements = withContext(Dispatchers.IO) {
                measurementDao.getAll(range.first, range.second)
            }
            _dailyMeasurements.value = dailyMeasurements
            _dayInstant.value = zonedDateTime
            _selectedMeasurement.value = dailyMeasurements.firstOrNull()
        }
    }

    private fun createDayRange(zonedDateTime: ZonedDateTime): Pair<ZonedDateTime, ZonedDateTime> {
        val start = zonedDateTime.run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 0, 0, 0, 0, zone)
        }
        val end = zonedDateTime.run {
            ZonedDateTime.of(year, monthValue, dayOfMonth, 23, 59, 59, 999, zone)
        }
        return Pair(start, end)
    }


}
