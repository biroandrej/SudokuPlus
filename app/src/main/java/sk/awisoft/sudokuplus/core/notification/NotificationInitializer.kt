package sk.awisoft.sudokuplus.core.notification

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager

@Singleton
class NotificationInitializer
@Inject
constructor(
    @param:ApplicationContext private val context: Context,
    private val notificationHelper: NotificationHelper,
    private val notificationSettings: NotificationSettingsManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun createChannels() {
        notificationHelper.createNotificationChannels()
    }

    /**
     * Schedules notification workers if enabled in settings.
     * This runs asynchronously to avoid blocking app startup.
     */
    fun scheduleWorkersIfNeeded(context: Context) {
        scope.launch {
            // Only schedule if we have notification permission
            if (!notificationHelper.hasNotificationPermission()) return@launch

            scheduleWorkersIfEnabled(context)
        }
    }

    /**
     * Internal method that schedules workers based on current settings.
     * Must be called from a coroutine context.
     */
    private suspend fun scheduleWorkersIfEnabled(context: Context) {
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
