package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class NotificationSettingsManager
@Inject
constructor(
    settingsDataStore: SettingsDataStore
) {
    private val dataStore = settingsDataStore.dataStore

    private val notificationPermissionRequestedKey =
        booleanPreferencesKey("notification_permission_requested")
    private val dailyChallengeNotificationEnabledKey =
        booleanPreferencesKey("daily_challenge_notification_enabled")
    private val dailyChallengeNotificationHourKey =
        intPreferencesKey("daily_challenge_notification_hour")
    private val dailyChallengeNotificationMinuteKey =
        intPreferencesKey("daily_challenge_notification_minute")
    private val streakReminderEnabledKey = booleanPreferencesKey("streak_reminder_enabled")
    private val streakReminderHourKey = intPreferencesKey("streak_reminder_hour")
    private val streakReminderMinuteKey = intPreferencesKey("streak_reminder_minute")

    // Track if we've already asked for notification permission
    val notificationPermissionRequested =
        dataStore.data.map { preferences ->
            preferences[notificationPermissionRequestedKey] ?: false
        }

    suspend fun setNotificationPermissionRequested(requested: Boolean) {
        dataStore.edit { settings ->
            settings[notificationPermissionRequestedKey] = requested
        }
    }

    // Daily challenge notification
    val dailyChallengeNotificationEnabled =
        dataStore.data.map { preferences ->
            preferences[dailyChallengeNotificationEnabledKey] ?: true // Default: enabled
        }

    suspend fun setDailyChallengeNotificationEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[dailyChallengeNotificationEnabledKey] = enabled
        }
    }

    val dailyChallengeNotificationHour =
        dataStore.data.map { preferences ->
            preferences[dailyChallengeNotificationHourKey] ?: 8
        }

    val dailyChallengeNotificationMinute =
        dataStore.data.map { preferences ->
            preferences[dailyChallengeNotificationMinuteKey] ?: 0
        }

    suspend fun setDailyChallengeNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { settings ->
            settings[dailyChallengeNotificationHourKey] = hour
            settings[dailyChallengeNotificationMinuteKey] = minute
        }
    }

    // Streak reminder
    val streakReminderEnabled =
        dataStore.data.map { preferences ->
            preferences[streakReminderEnabledKey] ?: true // Default: enabled
        }

    suspend fun setStreakReminderEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[streakReminderEnabledKey] = enabled
        }
    }

    val streakReminderHour =
        dataStore.data.map { preferences ->
            preferences[streakReminderHourKey] ?: 20
        }

    val streakReminderMinute =
        dataStore.data.map { preferences ->
            preferences[streakReminderMinuteKey] ?: 0
        }

    suspend fun setStreakReminderTime(hour: Int, minute: Int) {
        dataStore.edit { settings ->
            settings[streakReminderHourKey] = hour
            settings[streakReminderMinuteKey] = minute
        }
    }
}
