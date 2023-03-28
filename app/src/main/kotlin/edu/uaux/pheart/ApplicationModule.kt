package edu.uaux.pheart

import edu.uaux.pheart.preferences.PreferencesFragment
import org.koin.androidx.fragment.dsl.fragment
import org.koin.dsl.module

val applicationModule = module {
    fragment { PreferencesFragment() }
}