package edu.uaux.pheart.preferences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.singleChoice
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem

class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val preferenceScreen = screen(getApplication()) {
        collapseIcon = true

        switch(PreferenceKeys.PREF_KEY_ENABLE_REMINDERS) {
            title = "Send reminders"
            summary = "Get a reminder to measure your heart rate in regular intervals."
        }

        singleChoice(
            PreferenceKeys.PREF_KEY_INTERVAL_REMINDER,
            items = listOf(
                SelectionItem("30s", title = "30 seconds"),
                SelectionItem("30m", title = "30 minutes"),
                SelectionItem("1h", title = "1 hour"),
                SelectionItem("2h", title = "2 hours"),
                SelectionItem("4h", title = "4 hours"),
                SelectionItem("6h", title = "6 hours"),
                SelectionItem("8h", title = "8 hours"),
                SelectionItem("1d", title = "24 hours"),
            ),
        ) {
            title = "Reminder Interval"
            initialSelection = "30m"
            dependency = PreferenceKeys.PREF_KEY_ENABLE_REMINDERS
        }

        switch(PreferenceKeys.PREF_KEY_RESPECT_DO_NOT_DISTURB) {
            title = "Do not disturb"
            summary = "Ignore notifications when the phone is in \"do not disturb\" mode."
            defaultValue = true
            dependency = PreferenceKeys.PREF_KEY_ENABLE_REMINDERS
        }

    }
    val preferencesAdapter = PreferencesAdapter(preferenceScreen)
}