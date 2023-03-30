package edu.uaux.pheart.measure

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class MeasurementType : Parcelable {
    FACE,
    FINGER,
    ;
}