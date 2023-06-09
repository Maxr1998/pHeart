package edu.uaux.pheart.measure

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.database.MeasurementDao
import edu.uaux.pheart.database.get
import edu.uaux.pheart.preferences.PreferenceKeys
import edu.uaux.pheart.profile.BiologicalSex
import edu.uaux.pheart.profile.HeartRateInfo
import edu.uaux.pheart.statistics.units.TimeUnitUtil
import edu.uaux.pheart.util.ext.avgOfOrNull
import edu.uaux.pheart.util.ext.toast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.ZonedDateTime

class MeasureResultsViewModel(app: Application) : AndroidViewModel(app), KoinComponent {
    private val _comparedToYesterday = MutableLiveData<Int?>()
    val comparedToYesterday: LiveData<Int?> = _comparedToYesterday

    private val _comparedToLast7Days = MutableLiveData<Int?>()
    val comparedToLast7Days: LiveData<Int?> = _comparedToLast7Days

    private val _restingBpmGoodRange = MutableLiveData<IntRange>()
    val restingBpmGoodRange: LiveData<IntRange> = _restingBpmGoodRange

    private val _exercisingBpmGoodRange = MutableLiveData<IntRange>()
    val exercisingBpmGoodRange: LiveData<IntRange> = _exercisingBpmGoodRange

    private var initialized = false

    private val measurementDao: MeasurementDao by inject()
    private val sharedPreferences: SharedPreferences by inject()

    suspend fun readMeasurementResults(measurement: Measurement) {
        if (initialized) {
            getApplication<Application>().toast("Already initialized")
            return
        }

        _comparedToYesterday.value = getYesterdayAverage()?.let { avg -> measurement.bpm - avg }
        _comparedToLast7Days.value = getLast7DaysAverage()?.let { avg -> measurement.bpm - avg }

        val age = sharedPreferences.getInt(PreferenceKeys.PREF_KEY_AGE, 30)
        val sex = BiologicalSex.fromId(
            sharedPreferences.getInt(
                PreferenceKeys.PREF_KEY_SEX,
                PreferenceKeys.PREF_SEX_DEFAULT_VALUE,
            ),
        )

        _restingBpmGoodRange.value = HeartRateInfo.getRestingHeartRate(age, sex)
        _exercisingBpmGoodRange.value = HeartRateInfo.getExerciseHeartRange(age, sex)

        initialized = true
    }

    private suspend fun getYesterdayAverage(): Int? =
        measurementDao.get(TimeUnitUtil.createDayRange(ZonedDateTime.now().minusDays(1)))
            .avgOfOrNull { measurement -> measurement.bpm }

    private suspend fun getLast7DaysAverage(): Int? =
        measurementDao.get(TimeUnitUtil.createLast7DaysRange(ZonedDateTime.now()))
            .avgOfOrNull { measurement -> measurement.bpm }
}