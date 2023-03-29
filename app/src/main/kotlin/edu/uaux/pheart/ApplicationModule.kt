package edu.uaux.pheart

import android.app.Application
import androidx.room.Room
import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.database.AppDatabase
import edu.uaux.pheart.measure.MeasureSettingsFragment
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import edu.uaux.pheart.statistics.StatisticsFragment
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
}