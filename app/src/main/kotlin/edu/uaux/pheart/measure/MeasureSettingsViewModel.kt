package edu.uaux.pheart.measure

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.uaux.pheart.database.ActivityLevel

class MeasureSettingsViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        const val DEFAULT_DURATION = 30
        val DURATIONS = arrayOf(5, 15, DEFAULT_DURATION, 60)
    }

    var measurementType: MeasurementType = MeasurementType.FACE
    var activityLevel: ActivityLevel = ActivityLevel.SEATED
    var measurementDuration: Int = DEFAULT_DURATION
}