package edu.uaux.pheart.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents the activity level of the user at the time of the measurement.
 */
@Parcelize
enum class ActivityLevel : Parcelable {
    RELAXING,
    EXERCISING,
    ;
}