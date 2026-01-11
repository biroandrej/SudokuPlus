package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sk.awisoft.sudokuplus.ai.FirebaseTimestampProvider

@Singleton
class AIUsageManager @Inject constructor(
    settingsDataStore: SettingsDataStore,
    private val timestampProvider: FirebaseTimestampProvider
) {
    private val dataStore = settingsDataStore.dataStore

    companion object {
        private val AI_HINTS_USED_TODAY = intPreferencesKey("ai_hints_used_today")
        private val AI_HINTS_LAST_RESET_DATE = longPreferencesKey("ai_hints_last_reset_date")
        private val AD_GRANTED_AI_HINTS = intPreferencesKey("ad_granted_ai_hints")
        const val FREE_AI_HINTS_PER_DAY = 1
    }

    val freeAIHintsRemaining: Flow<Int> = dataStore.data.map { preferences ->
        val usedToday = preferences[AI_HINTS_USED_TODAY] ?: 0
        val adGranted = preferences[AD_GRANTED_AI_HINTS] ?: 0
        maxOf(0, FREE_AI_HINTS_PER_DAY - usedToday + adGranted)
    }

    suspend fun canUseFreeAIHint(): Boolean {
        resetDailyCounterIfNeeded()
        return freeAIHintsRemaining.first() > 0
    }

    suspend fun consumeFreeAIHint(): Boolean {
        resetDailyCounterIfNeeded()

        var consumed = false
        dataStore.edit { settings ->
            val adGranted = settings[AD_GRANTED_AI_HINTS] ?: 0
            if (adGranted > 0) {
                // First consume ad-granted hints
                settings[AD_GRANTED_AI_HINTS] = adGranted - 1
                consumed = true
            } else {
                // Then consume free daily hint
                val usedToday = settings[AI_HINTS_USED_TODAY] ?: 0
                if (usedToday < FREE_AI_HINTS_PER_DAY) {
                    settings[AI_HINTS_USED_TODAY] = usedToday + 1
                    consumed = true
                }
            }
        }
        return consumed
    }

    suspend fun grantAIHintFromAd() {
        dataStore.edit { settings ->
            val current = settings[AD_GRANTED_AI_HINTS] ?: 0
            settings[AD_GRANTED_AI_HINTS] = current + 1
        }
    }

    private suspend fun resetDailyCounterIfNeeded() {
        val serverDate = timestampProvider.getServerDate()
        val serverDateEpoch = serverDate.toEpochDay()

        dataStore.edit { settings ->
            val lastResetDate = settings[AI_HINTS_LAST_RESET_DATE] ?: 0L

            // If server date is newer than last reset, reset daily counter only
            // Ad-granted hints persist across days - they were earned by watching ads
            if (serverDateEpoch > lastResetDate) {
                settings[AI_HINTS_USED_TODAY] = 0
                settings[AI_HINTS_LAST_RESET_DATE] = serverDateEpoch
            }
            // If server date is older (clock was moved back), keep current state
            // This prevents abuse by setting clock backwards
        }
    }

    suspend fun getUsedTodayCount(): Int {
        resetDailyCounterIfNeeded()
        return dataStore.data.first()[AI_HINTS_USED_TODAY] ?: 0
    }
}
