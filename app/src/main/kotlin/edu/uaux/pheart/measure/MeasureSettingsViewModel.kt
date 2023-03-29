package edu.uaux.pheart.measure

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import edu.uaux.pheart.database.ActivityLevel

class MeasureSettingsViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        val DURATIONS = arrayOf(5, 15, 30, 60)
    }

    var measurementType: MeasurementType = MeasurementType.FACE
    var activityLevel: ActivityLevel = ActivityLevel.SEATED
    var measurementDuration: Int = 30
}