package edu.uaux.pheart.database

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ActivityLevel : Parcelable {
    RELAXING,
    EXERCISING,
    ;
}