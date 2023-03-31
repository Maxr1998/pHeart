package edu.uaux.pheart.preferences

import edu.uaux.pheart.profile.BiologicalSex

object PreferenceKeys {
    const val PREF_KEY_INTERVAL_REMINDER = "interval_reminder"
    const val PREF_KEY_ENABLE_REMINDERS = "enable_reminders"
    const val PREF_KEY_AGE = "age"
    const val PREF_AGE_DEFAULT_VALUE = -1
    const val PREF_KEY_SEX = "sex"
    val PREF_SEX_DEFAULT_VALUE = BiologicalSex.NONE.id
    const val PREF_KEY_SEND_MANUAL_NOTIFICATION = "send_manual_notification"
}