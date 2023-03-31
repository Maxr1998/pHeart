package edu.uaux.pheart

import android.app.Application
import android.content.Context
import androidx.room.Room
import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.database.AppDatabase
import edu.uaux.pheart.measure.MeasureSettingsFragment
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import edu.uaux.pheart.statistics.StatisticsFragment
import edu.uaux.pheart.util.NotificationService
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module

val applicationModule = module {
    fragment { StatisticsFragment() }
    fragment { AllResultsFragment() }
    fragment { MeasureSettingsFragment() }
    fragment { ProfileFragment() }
    fragment { PreferencesFragment() }
    single {
        Room.databaseBuilder(get<Application>(), AppDatabase::class.java, name = "db")
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        val context = get<Context>()
        context.getSharedPreferences(
            "${context.packageName}_preferences",
            Context.MODE_PRIVATE,
        )
    }
    single { get<AppDatabase>().measurementDao() }
    single { NotificationService(get()) }
}