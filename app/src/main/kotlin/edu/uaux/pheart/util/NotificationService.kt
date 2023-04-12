package edu.uaux.pheart.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import edu.uaux.pheart.MainActivity
import edu.uaux.pheart.R

/**
 * Streamlines sending notifications.
 */
class NotificationService(
    private val context: Context,
) {
    companion object {
        private const val DEFAULT_CHANNEL_ID = "default"
        private const val DEFAULT_CHANNEL_NAME = "Default Channel"
        private const val DEFAULT_CHANNEL_DESCRIPTION = "The default notification channel."

        private var currentNotificationCount = 0
    }

    /**
     * Should be run at the start of the app.
     */
    fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(
            DEFAULT_CHANNEL_ID,
            DEFAULT_CHANNEL_NAME,
            importance,
        ).apply {
            description = DEFAULT_CHANNEL_DESCRIPTION
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendMeasurementReminder() {
        sendNotification(
            icon = R.drawable.ic_heart_24,
            titleRes = R.string.notification_debug_title,
            onMissingPermission = {
                Toast.makeText(context, "Missing notification permission", Toast.LENGTH_SHORT).show()
            },
        )
    }

    private fun sendNotification(
        @DrawableRes icon: Int,
        @StringRes titleRes: Int,
        onMissingPermission: (() -> Unit)? = null,
    ) {
        val pendingIntent = createPendingIntent()

        // channel given to builder is ignored on Android 7.1 and older
        val builder = NotificationCompat.Builder(
            context,
            DEFAULT_CHANNEL_ID,
        )
            .setSmallIcon(icon)
            .setContentTitle(context.getString(titleRes))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                onMissingPermission?.invoke()
                return@with
            }

            notify(currentNotificationCount++, builder.build())
        }
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            // do not create back stack navigation
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(MainActivity.EXTRA_START_FRAGMENT, R.id.screen_add_measurement)
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}