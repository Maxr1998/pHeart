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

    private val _statisticsMode: MutableLiveData<StatisticsFragmentMode> = MutableLiveData(StatisticsFragmentMode.DAILY)

    val selectedMeasurement: LiveData<AverageBpm?>
        get() = _selectedMeasurement
    val dailyMeasurements: LiveData<List<Measurement>>
        get() = _dailyMeasurements
    val dayInstant: LiveData<ZonedDateTime?>
        get() = _dayInstant
    val statisticsMode: LiveData<StatisticsFragmentMode>
        get() = _statisticsMode

    fun loadToday() {
        loadDay(ZonedDateTime.now(ZoneId.systemDefault()))
    }

    private fun loadDay(zonedDateTime: ZonedDateTime) {
        val range = StatisticsUtils.createDayRange(zonedDateTime)
        this.viewModelScope.launch {
            val dailyMeasurements = withContext(Dispatchers.IO) {
                measurementDao.get(range)
            }
            _dailyMeasurements.value = dailyMeasurements
            _dayInstant.value = zonedDateTime
        }
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

    fun switchTo(mode: StatisticsFragmentMode) {
        if (mode != _statisticsMode.value) {
            _statisticsMode.value = mode
        }
    }

    fun toNextDay() {
        _dayInstant.value?.let {
            loadDay(it.plusDays(1))
        }
    }

    fun toPreviousDay() {
        _dayInstant.value?.let {
            loadDay(it.minusDays(1))
        }
    }
}
