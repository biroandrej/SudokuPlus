package sk.awisoft.sudokuplus.ui.settings.notifications

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.notification.DailyChallengeNotificationWorker
import sk.awisoft.sudokuplus.core.notification.NotificationHelper
import sk.awisoft.sudokuplus.core.notification.StreakReminderWorker
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import javax.inject.Inject

@HiltViewModel
class SettingsNotificationsViewModel @Inject constructor(
    private val settings: NotificationSettingsManager,
    private val notificationHelper: NotificationHelper,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val dailyChallengeEnabled = settings.dailyChallengeNotificationEnabled
    val dailyChallengeHour = settings.dailyChallengeNotificationHour
    val dailyChallengeMinute = settings.dailyChallengeNotificationMinute

    val streakReminderEnabled = settings.streakReminderEnabled
    val streakReminderHour = settings.streakReminderHour
    val streakReminderMinute = settings.streakReminderMinute

    val dailyChallengeTime = combine(dailyChallengeHour, dailyChallengeMinute) { hour, minute ->
        Pair(hour, minute)
    }

    val streakReminderTime = combine(streakReminderHour, streakReminderMinute) { hour, minute ->
        Pair(hour, minute)
    }

    fun hasNotificationPermission(): Boolean {
        return notificationHelper.hasNotificationPermission()
    }

    fun updateDailyChallengeEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setDailyChallengeNotificationEnabled(enabled)
            if (enabled) {
                val hour = settings.dailyChallengeNotificationHour.first()
                val minute = settings.dailyChallengeNotificationMinute.first()
                DailyChallengeNotificationWorker.schedule(context, hour, minute)
            } else {
                DailyChallengeNotificationWorker.cancel(context)
            }
        }
    }

    fun updateDailyChallengeTime(hour: Int, minute: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setDailyChallengeNotificationTime(hour, minute)
            // Reschedule if enabled
            val enabled = settings.dailyChallengeNotificationEnabled.first()
            if (enabled) {
                DailyChallengeNotificationWorker.schedule(context, hour, minute)
            }
        }
    }

    fun updateStreakReminderEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setStreakReminderEnabled(enabled)
            if (enabled) {
                val hour = settings.streakReminderHour.first()
                val minute = settings.streakReminderMinute.first()
                StreakReminderWorker.schedule(context, hour, minute)
            } else {
                StreakReminderWorker.cancel(context)
            }
        }
    }

    fun updateStreakReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setStreakReminderTime(hour, minute)
            // Reschedule if enabled
            val enabled = settings.streakReminderEnabled.first()
            if (enabled) {
                StreakReminderWorker.schedule(context, hour, minute)
            }
        }
    }
}
