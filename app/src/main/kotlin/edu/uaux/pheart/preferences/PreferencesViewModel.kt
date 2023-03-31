package edu.uaux.pheart.preferences

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.Maxr1998.modernpreferences.helpers.singleChoice
import de.Maxr1998.modernpreferences.helpers.switch
import de.Maxr1998.modernpreferences.preferences.choice.SelectionItem
import edu.uaux.pheart.R

class PreferencesViewModel(app: Application) : AndroidViewModel(app) {
    private val preferenceScreen = screen(getApplication()) {
        collapseIcon = true

        switch(PreferenceKeys.PREF_KEY_ENABLE_REMINDERS) {
            titleRes = R.string.notification_enable_title
            summaryRes = R.string.notification_enable_desc
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
            titleRes = R.string.notification_reminder_interval_title
            initialSelection = "30m"
            dependency = PreferenceKeys.PREF_KEY_ENABLE_REMINDERS
        }

        pref(PreferenceKeys.PREF_KEY_SEND_MANUAL_NOTIFICATION) {
            titleRes = R.string.notification_send_debug_title
            onClick {
                Toast.makeText(this@PreferencesViewModel.getApplication(), "Notification sent", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        }
    }
    val preferencesAdapter = PreferencesAdapter(preferenceScreen)
}