package sk.awisoft.sudokuplus.core.notification

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationHelper: NotificationHelper,
    private val notificationSettings: NotificationSettingsManager
) {
    fun createChannels() {
        notificationHelper.createNotificationChannels()
    }

    suspend fun scheduleWorkersIfEnabled() {
        val dailyChallengeEnabled = notificationSettings.dailyChallengeNotificationEnabled.first()
        if (dailyChallengeEnabled) {
            val hour = notificationSettings.dailyChallengeNotificationHour.first()
            val minute = notificationSettings.dailyChallengeNotificationMinute.first()
            DailyChallengeNotificationWorker.schedule(context, hour, minute)
        }

        val streakReminderEnabled = notificationSettings.streakReminderEnabled.first()
        if (streakReminderEnabled) {
            val hour = notificationSettings.streakReminderHour.first()
            val minute = notificationSettings.streakReminderMinute.first()
            StreakReminderWorker.schedule(context, hour, minute)
        }
    }
}
