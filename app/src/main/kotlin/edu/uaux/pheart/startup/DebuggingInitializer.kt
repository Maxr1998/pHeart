package edu.uaux.pheart.startup


import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import androidx.startup.Initializer
import timber.log.Timber

class DebuggingInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val inDebugMode = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (inDebugMode) {
            setStrictMode()
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .penaltyLog()
                .penaltyDialog()
                .build(),
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build(),
        )
    }

    override fun dependencies(): List<Class<Initializer<*>>> = emptyList()
}