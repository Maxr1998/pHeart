package edu.uaux.pheart.startup

import android.content.Context
import androidx.startup.AppInitializer
import androidx.startup.Initializer
import edu.uaux.pheart.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class KoinInitializer : Initializer<KoinApplication> {
    override fun create(context: Context): KoinApplication {
        val koinApp = startKoin {
            androidContext(context)
            modules(applicationModule)
        }
        return koinApp
    }

    override fun dependencies(): List<Class<Initializer<*>>> = emptyList()

    companion object {
        /**
         * Ensure Koin has been initialized through the [AppInitializer]
         */
        fun ensureInitialized(applicationContext: Context) {
            AppInitializer.getInstance(applicationContext).initializeComponent(KoinInitializer::class.java)
        }
    }
}