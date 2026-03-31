package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map

@Singleton
class PlayGamesSettingsManager
@Inject
constructor(
    settingsDataStore: SettingsDataStore
) {
    private val dataStore = settingsDataStore.dataStore

    private val playGamesEnabledKey = booleanPreferencesKey("play_games_enabled")
    private val homePromptDismissedKey = booleanPreferencesKey("play_games_home_prompt_dismissed")

    val playGamesEnabled =
        dataStore.data.map { preferences ->
            preferences[playGamesEnabledKey] ?: false
        }

    suspend fun setPlayGamesEnabled(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[playGamesEnabledKey] = enabled
        }
    }

    val homePromptDismissed =
        dataStore.data.map { preferences ->
            preferences[homePromptDismissedKey] ?: false
        }

    suspend fun setHomePromptDismissed(dismissed: Boolean) {
        dataStore.edit { settings ->
            settings[homePromptDismissedKey] = dismissed
        }
    }
}
