package edu.uaux.pheart.measure

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.util.ext.toast
import edu.uaux.pheart.database.MeasurementDao
import edu.uaux.pheart.statistics.StatisticsUtils
import edu.uaux.pheart.statistics.get
import edu.uaux.pheart.util.avgOfOrNull
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime

class MeasureResultsViewModel(app: Application) : AndroidViewModel(app), KoinComponent {
    private val _comparedToYesterday = MutableLiveData<Int?>()
    val comparedToYesterday: LiveData<Int?> = _comparedToYesterday

    private val _comparedToLast7Days = MutableLiveData<Int?>()
    val comparedToLast7Days: LiveData<Int?> = _comparedToLast7Days

    private var initialized = false

    private val measurementDao: MeasurementDao by inject()

    suspend fun readMeasurementResults(measurement: Measurement) {
        if (initialized) {
            getApplication<Application>().toast("Already initialized")
            return
        }

        _comparedToYesterday.value = getYesterdayAverage()?.let{ avg -> measurement.bpm - avg}
        _comparedToLast7Days.value = getLast7DaysAverage()?.let{ avg -> measurement.bpm - avg}

        initialized = true
    }

    private suspend fun getYesterdayAverage(): Int? =
        measurementDao.get(StatisticsUtils.createDayRange(ZonedDateTime.now().minusDays(1)))
            .avgOfOrNull { measurement -> measurement.bpm }

    private suspend fun getLast7DaysAverage(): Int? =
        measurementDao.get(StatisticsUtils.createLast7DaysRange(ZonedDateTime.now()))
            .avgOfOrNull { measurement -> measurement.bpm }

}