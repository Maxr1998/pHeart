package edu.uaux.pheart.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
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

    private val _selectedMeasurement: MutableLiveData<AverageBpm?> = MutableLiveData<AverageBpm?>(null)
    private val _dailyMeasurements: MutableLiveData<List<Measurement>> = MutableLiveData(emptyList())
    private val _dayInstant: MutableLiveData<ZonedDateTime?> = MutableLiveData(null)

    val selectedMeasurement: LiveData<AverageBpm?>
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

    val onChartValueSelectedListener = object : OnChartValueSelectedListener {
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            if (e == null || e.data == null) {
                onNothingSelected()
                return
            }

            val averageBpm = e.data as AverageBpm
            if (averageBpm.avgBpm <= 0) {
                onNothingSelected()
                return
            }
            _selectedMeasurement.value = e.data as AverageBpm
        }

        override fun onNothingSelected() {
            _selectedMeasurement.value = null
        }
    }

    data class AverageBpm(val timeOfDay: TimeOfDay, val avgBpm: Int)
}