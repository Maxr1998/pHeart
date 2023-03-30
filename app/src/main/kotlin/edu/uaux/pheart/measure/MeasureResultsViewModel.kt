package edu.uaux.pheart.measure

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import edu.uaux.pheart.database.Measurement
import edu.uaux.pheart.util.ext.toast

class MeasureResultsViewModel(app: Application) : AndroidViewModel(app) {
    private val _comparedToYesterday = MutableLiveData<Int>()
    val comparedToYesterday: LiveData<Int> = _comparedToYesterday

    private val _comparedToLast7Days = MutableLiveData<Int>()
    val comparedToLast7Days: LiveData<Int> = _comparedToLast7Days

    private var initialized = false

    fun readMeasurementResults(measurement: Measurement) {
        if (initialized) {
            getApplication<Application>().toast("Already initialized")
            return
        }

        _comparedToYesterday.value = 12 // TODO
        _comparedToLast7Days.value = 6 // TODO

        initialized = true
    }
}