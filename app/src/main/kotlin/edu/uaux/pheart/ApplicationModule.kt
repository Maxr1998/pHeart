package edu.uaux.pheart

import edu.uaux.pheart.all_results.AllResultsFragment
import edu.uaux.pheart.preferences.PreferencesFragment
import edu.uaux.pheart.profile.ProfileFragment
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module

val applicationModule = module {
    fragment { PreferencesFragment() }
    fragment { ProfileFragment() }
    fragment { AllResultsFragment() }
}