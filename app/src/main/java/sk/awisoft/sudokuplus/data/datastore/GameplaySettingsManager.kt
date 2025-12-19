package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class GameplaySettingsManager @Inject constructor(
    settingsDataStore: SettingsDataStore
) {
    private val dataStore = settingsDataStore.dataStore

    private val inputMethodKey = intPreferencesKey("input_method")
    private val mistakesLimitKey = booleanPreferencesKey("mistakes_limit")
    private val hintsDisabledKey = booleanPreferencesKey("hints_disabled")
    private fun hintsRemainingKey(gameUid: Long) = intPreferencesKey("hints_remaining_$gameUid")
    private val interstitialGamesCompletedKey = intPreferencesKey("interstitial_games_completed")
    private val interstitialNextThresholdKey = intPreferencesKey("interstitial_next_threshold")
    private val timerKey = booleanPreferencesKey("timer")
    private val resetTimerKey = booleanPreferencesKey("timer_reset")
    private val firstGameKey = booleanPreferencesKey("first_game")
    private val funKeyboardOverNumKey = booleanPreferencesKey("fun_keyboard_over_numbers")
    private val saveSelectedGameDifficultyTypeKey = booleanPreferencesKey("save_last_selected_difficulty_type")
    private val lastSelectedGameDifficultyTypeKey = stringPreferencesKey("last_selected_difficulty_type")

    // Input method
    val inputMethod = dataStore.data.map { preferences ->
        preferences[inputMethodKey] ?: PreferencesConstants.DEFAULT_INPUT_METHOD
    }

    suspend fun setInputMethod(value: Int) {
        dataStore.edit { settings ->
            settings[inputMethodKey] = value
        }
    }

    // Mistakes limit
    val mistakesLimit = dataStore.data.map { preferences ->
        preferences[mistakesLimitKey] ?: PreferencesConstants.DEFAULT_MISTAKES_LIMIT
    }

    suspend fun setMistakesLimit(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[mistakesLimitKey] = enabled
        }
    }

    // Hints
    val hintsDisabled = dataStore.data.map { preferences ->
        preferences[hintsDisabledKey] ?: PreferencesConstants.DEFAULT_HINTS_DISABLED
    }

    suspend fun setHintsDisabled(disabled: Boolean) {
        dataStore.edit { settings ->
            settings[hintsDisabledKey] = disabled
        }
    }

    fun hintsRemaining(gameUid: Long) = dataStore.data.map { preferences ->
        if (gameUid <= 0L) {
            PreferencesConstants.DEFAULT_HINTS_PER_GAME
        } else {
            preferences[hintsRemainingKey(gameUid)] ?: PreferencesConstants.DEFAULT_HINTS_PER_GAME
        }
    }

    suspend fun resetHintsRemaining(gameUid: Long) {
        if (gameUid <= 0L) return
        dataStore.edit { settings ->
            settings[hintsRemainingKey(gameUid)] = PreferencesConstants.DEFAULT_HINTS_PER_GAME
        }
    }

    suspend fun tryConsumeHint(gameUid: Long): Boolean {
        if (gameUid <= 0L) return false
        var consumed = false
        dataStore.edit { settings ->
            val current = settings[hintsRemainingKey(gameUid)] ?: PreferencesConstants.DEFAULT_HINTS_PER_GAME
            if (current > 0) {
                settings[hintsRemainingKey(gameUid)] = current - 1
                consumed = true
            }
        }
        return consumed
    }

    suspend fun grantHints(gameUid: Long, amount: Int = 1) {
        if (gameUid <= 0L) return
        if (amount <= 0) return
        dataStore.edit { settings ->
            val current = settings[hintsRemainingKey(gameUid)] ?: PreferencesConstants.DEFAULT_HINTS_PER_GAME
            settings[hintsRemainingKey(gameUid)] = current + amount
        }
    }

    // Interstitial ads
    suspend fun shouldShowInterstitialAfterGameComplete(): Boolean {
        var shouldShow = false
        dataStore.edit { settings ->
            val nextThreshold = settings[interstitialNextThresholdKey] ?: Random.nextInt(2, 4)
            val completedCount = (settings[interstitialGamesCompletedKey] ?: 0) + 1
            if (completedCount >= nextThreshold) {
                settings[interstitialGamesCompletedKey] = 0
                settings[interstitialNextThresholdKey] = Random.nextInt(2, 4)
                shouldShow = true
            } else {
                settings[interstitialGamesCompletedKey] = completedCount
                settings[interstitialNextThresholdKey] = nextThreshold
            }
        }
        return shouldShow
    }

    // Timer
    val timerEnabled = dataStore.data.map { preferences ->
        preferences[timerKey] ?: PreferencesConstants.DEFAULT_SHOW_TIMER
    }

    suspend fun setTimer(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[timerKey] = enabled
        }
    }

    val resetTimerEnabled = dataStore.data.map { preferences ->
        preferences[resetTimerKey] ?: PreferencesConstants.DEFAULT_GAME_RESET_TIMER
    }

    suspend fun setResetTimer(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[resetTimerKey] = enabled
        }
    }

    // First game
    val firstGame = dataStore.data.map { preferences ->
        preferences[firstGameKey] ?: true
    }

    suspend fun setFirstGame(value: Boolean) {
        dataStore.edit { settings ->
            settings[firstGameKey] = value
        }
    }

    // Function keyboard position
    val funKeyboardOverNumbers = dataStore.data.map { prefs ->
        prefs[funKeyboardOverNumKey] ?: PreferencesConstants.DEFAULT_FUN_KEYBOARD_OVER_NUM
    }

    suspend fun setFunKeyboardOverNum(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[funKeyboardOverNumKey] = enabled
        }
    }

    // Save selected game difficulty/type
    val saveSelectedGameDifficultyType = dataStore.data.map { prefs ->
        prefs[saveSelectedGameDifficultyTypeKey] ?: PreferencesConstants.DEFAULT_SAVE_LAST_SELECTED_DIFF_TYPE
    }

    suspend fun setSaveSelectedGameDifficultyType(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[saveSelectedGameDifficultyTypeKey] = enabled
        }
    }

    val lastSelectedGameDifficultyType = dataStore.data.map { prefs ->
        var gameDifficulty = GameDifficulty.Easy
        var gameType = GameType.Default9x9

        val key = prefs[lastSelectedGameDifficultyTypeKey] ?: ""
        if (key.isNotEmpty() && key.contains(";")) {
            gameDifficulty = when (key.substring(0, key.indexOf(";"))) {
                "0" -> GameDifficulty.Unspecified
                "1" -> GameDifficulty.Simple
                "2" -> GameDifficulty.Easy
                "3" -> GameDifficulty.Moderate
                "4" -> GameDifficulty.Hard
                "5" -> GameDifficulty.Challenge
                "6" -> GameDifficulty.Custom
                else -> GameDifficulty.Easy
            }
            gameType = when (key.substring(key.indexOf(";") + 1)) {
                "0" -> GameType.Unspecified
                "1" -> GameType.Default9x9
                "2" -> GameType.Default12x12
                "3" -> GameType.Default6x6
                "4" -> GameType.Killer9x9
                "5" -> GameType.Killer12x12
                "6" -> GameType.Killer6x6
                else -> GameType.Default9x9
            }
        }
        Pair(gameDifficulty, gameType)
    }

    suspend fun setLastSelectedGameDifficultyType(difficulty: GameDifficulty, type: GameType) {
        dataStore.edit { settings ->
            var difficultyAndType = when (difficulty) {
                GameDifficulty.Unspecified -> "0"
                GameDifficulty.Simple -> "1"
                GameDifficulty.Easy -> "2"
                GameDifficulty.Moderate -> "3"
                GameDifficulty.Hard -> "4"
                GameDifficulty.Challenge -> "5"
                GameDifficulty.Custom -> "6"
            }
            difficultyAndType += ";"
            difficultyAndType += when (type) {
                GameType.Unspecified -> "0"
                GameType.Default9x9 -> "1"
                GameType.Default12x12 -> "2"
                GameType.Default6x6 -> "3"
                GameType.Killer9x9 -> "4"
                GameType.Killer12x12 -> "5"
                GameType.Killer6x6 -> "6"
            }
            settings[lastSelectedGameDifficultyTypeKey] = difficultyAndType
        }
    }
}
