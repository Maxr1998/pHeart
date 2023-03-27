package edu.uaux.pheart

import android.app.Application
import com.google.android.material.color.DynamicColors

class PHeartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}