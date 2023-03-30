package edu.uaux.pheart.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ActivityLevel : Parcelable {
    RELAXED,
    SEATED,
    LIGHT_EXERCISE,
    HEAVY_EXERCISE,
    ;
}