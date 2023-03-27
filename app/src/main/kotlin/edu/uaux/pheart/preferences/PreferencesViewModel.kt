package edu.uaux.pheart.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.switch

class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val preferenceScreen = screen(getApplication()) {
        collapseIcon = true

        switch(PreferenceKeys.PREF_KEY_ENABLE_NOTIFICATIONS) {
            title = "Enable notifications"
        }
    }
    val preferencesAdapter = PreferencesAdapter(preferenceScreen)
}