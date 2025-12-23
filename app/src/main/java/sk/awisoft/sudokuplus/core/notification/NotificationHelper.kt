package sk.awisoft.sudokuplus.core.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import sk.awisoft.sudokuplus.MainActivity
import sk.awisoft.sudokuplus.R

@Singleton
class NotificationHelper
@Inject
constructor(
    @param:ApplicationContext private val context: Context
) {
    companion object {
        const val CHANNEL_DAILY_CHALLENGE = "daily_challenge"
        const val CHANNEL_STREAK_REMINDER = "streak_reminder"

        const val NOTIFICATION_ID_DAILY_CHALLENGE = 1001
        const val NOTIFICATION_ID_STREAK_REMINDER = 1002
    }

    fun createNotificationChannels() {
        val dailyChallengeChannel =
            NotificationChannel(
                CHANNEL_DAILY_CHALLENGE,
                context.getString(R.string.notification_channel_daily_challenge),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_daily_challenge_desc)
            }

        val streakReminderChannel =
            NotificationChannel(
                CHANNEL_STREAK_REMINDER,
                context.getString(R.string.notification_channel_streak_reminder),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_streak_reminder_desc)
            }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(dailyChallengeChannel)
        notificationManager.createNotificationChannel(streakReminderChannel)
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showDailyChallengeNotification() {
        if (!hasNotificationPermission()) return

        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("destination", "daily_challenge")
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                NOTIFICATION_ID_DAILY_CHALLENGE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(context, CHANNEL_DAILY_CHALLENGE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_daily_challenge_title))
                .setContentText(context.getString(R.string.notification_daily_challenge_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(
            context
        ).notify(NOTIFICATION_ID_DAILY_CHALLENGE, notification)
    }

    fun showStreakReminderNotification(currentStreak: Int) {
        if (!hasNotificationPermission()) return

        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                NOTIFICATION_ID_STREAK_REMINDER,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val text =
            if (currentStreak > 0) {
                context.getString(
                    R.string.notification_streak_reminder_text_with_streak,
                    currentStreak
                )
            } else {
                context.getString(R.string.notification_streak_reminder_text)
            }

        val notification =
            NotificationCompat.Builder(context, CHANNEL_STREAK_REMINDER)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.notification_streak_reminder_title))
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat.from(
            context
        ).notify(NOTIFICATION_ID_STREAK_REMINDER, notification)
    }

    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
