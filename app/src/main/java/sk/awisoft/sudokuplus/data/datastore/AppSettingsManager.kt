package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import sk.awisoft.sudokuplus.core.PreferencesConstants

/**
 * Core app settings manager.
 * For specific settings, use the dedicated managers:
 * - [GameplaySettingsManager] - Input method, mistakes, hints, timer, etc.
 * - [AssistanceSettingsManager] - Highlighting, auto-erase notes, advanced hints
 * - [BackupSettingsManager] - Backup URI, intervals, dates
 * - [NotificationSettingsManager] - Daily challenge and streak notifications
 */
@Singleton
class AppSettingsManager
@Inject
constructor(
    settingsDataStore: SettingsDataStore,
    val gameplay: GameplaySettingsManager,
    val assistance: AssistanceSettingsManager,
    val backup: BackupSettingsManager,
    val notification: NotificationSettingsManager
) {
    private val dataStore = settingsDataStore.dataStore

    private val firstLaunchKey = booleanPreferencesKey("first_launch")
    private val fontSizeKey = intPreferencesKey("board_font_size")
    private val keepScreenOnKey = booleanPreferencesKey("keep_screen_on")
    private val dateFormatKey = stringPreferencesKey("date_format")

    // First launch
    val firstLaunch =
        dataStore.data.map { preferences ->
            preferences[firstLaunchKey] ?: true
        }

    suspend fun setFirstLaunch(value: Boolean) {
        dataStore.edit { settings ->
            settings[firstLaunchKey] = value
        }
    }

    // Font size
    val fontSize =
        dataStore.data.map { preferences ->
            preferences[fontSizeKey] ?: PreferencesConstants.DEFAULT_FONT_SIZE_FACTOR
        }

    suspend fun setFontSize(value: Int) {
        dataStore.edit { settings ->
            settings[fontSizeKey] = value
        }
    }

    // Keep screen on
    val keepScreenOn =
        dataStore.data.map { preferences ->
            preferences[keepScreenOnKey] ?: PreferencesConstants.DEFAULT_KEEP_SCREEN_ON
        }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[keepScreenOnKey] = enabled
        }
    }

    // Date format
    val dateFormat =
        dataStore.data.map { prefs ->
            prefs[dateFormatKey] ?: ""
        }

    suspend fun setDateFormat(format: String) {
        dataStore.edit { settings ->
            settings[dateFormatKey] = format
        }
    }

    // ===========================================
    // Delegation to GameplaySettingsManager
    // ===========================================
    val inputMethod get() = gameplay.inputMethod

    suspend fun setInputMethod(value: Int) = gameplay.setInputMethod(value)

    val mistakesLimit get() = gameplay.mistakesLimit

    suspend fun setMistakesLimit(enabled: Boolean) = gameplay.setMistakesLimit(enabled)

    val hintsDisabled get() = gameplay.hintsDisabled

    suspend fun setHintsDisabled(disabled: Boolean) = gameplay.setHintsDisabled(disabled)

    fun hintsRemaining(gameUid: Long) = gameplay.hintsRemaining(gameUid)

    suspend fun resetHintsRemaining(gameUid: Long) = gameplay.resetHintsRemaining(gameUid)

    suspend fun tryConsumeHint(gameUid: Long) = gameplay.tryConsumeHint(gameUid)

    suspend fun grantHints(gameUid: Long, amount: Int = 1) = gameplay.grantHints(gameUid, amount)

    suspend fun shouldShowInterstitialAfterGameComplete() =
        gameplay.shouldShowInterstitialAfterGameComplete()

    val timerEnabled get() = gameplay.timerEnabled

    suspend fun setTimer(enabled: Boolean) = gameplay.setTimer(enabled)

    val resetTimerEnabled get() = gameplay.resetTimerEnabled

    suspend fun setResetTimer(enabled: Boolean) = gameplay.setResetTimer(enabled)

    val firstGame get() = gameplay.firstGame

    suspend fun setFirstGame(value: Boolean) = gameplay.setFirstGame(value)

    val funKeyboardOverNumbers get() = gameplay.funKeyboardOverNumbers

    suspend fun setFunKeyboardOverNum(enabled: Boolean) = gameplay.setFunKeyboardOverNum(
        enabled
    )

    val saveSelectedGameDifficultyType get() = gameplay.saveSelectedGameDifficultyType

    suspend fun setSaveSelectedGameDifficultyType(enabled: Boolean) =
        gameplay.setSaveSelectedGameDifficultyType(
            enabled
        )

    val lastSelectedGameDifficultyType get() = gameplay.lastSelectedGameDifficultyType

    suspend fun setLastSelectedGameDifficultyType(
        difficulty: sk.awisoft.sudokuplus.core.qqwing.GameDifficulty,
        type: sk.awisoft.sudokuplus.core.qqwing.GameType
    ) = gameplay.setLastSelectedGameDifficultyType(difficulty, type)

    // ===========================================
    // Delegation to AssistanceSettingsManager
    // ===========================================
    val highlightMistakes get() = assistance.highlightMistakes

    suspend fun setHighlightMistakes(value: Int) = assistance.setHighlightMistakes(value)

    val highlightIdentical get() = assistance.highlightIdentical

    suspend fun setSameValuesHighlight(enabled: Boolean) = assistance.setSameValuesHighlight(
        enabled
    )

    val remainingUse get() = assistance.remainingUse

    suspend fun setRemainingUse(enabled: Boolean) = assistance.setRemainingUse(enabled)

    val positionLines get() = assistance.positionLines

    suspend fun setPositionLines(enabled: Boolean) = assistance.setPositionLines(enabled)

    val autoEraseNotes get() = assistance.autoEraseNotes

    suspend fun setAutoEraseNotes(enabled: Boolean) = assistance.setAutoEraseNotes(enabled)

    val advancedHintEnabled get() = assistance.advancedHintEnabled

    suspend fun setAdvancedHint(enabled: Boolean) = assistance.setAdvancedHint(enabled)

    val advancedHintSettings get() = assistance.advancedHintSettings

    suspend fun updateAdvancedHintSettings(
        ahSettings: sk.awisoft.sudokuplus.core.qqwing.advanced_hint.AdvancedHintSettings
    ) = assistance.updateAdvancedHintSettings(ahSettings)

    // ===========================================
    // Delegation to BackupSettingsManager
    // ===========================================
    val backupUri get() = backup.backupUri

    suspend fun setBackupUri(uri: String) = backup.setBackupUri(uri)

    val autoBackupInterval get() = backup.autoBackupInterval

    suspend fun setAutoBackupInterval(hours: Long) = backup.setAutoBackupInterval(hours)

    val autoBackupsNumber get() = backup.autoBackupsNumber

    suspend fun setAutoBackupsNumber(value: Int) = backup.setAutoBackupsNumber(value)

    val lastBackupDate get() = backup.lastBackupDate

    suspend fun setLastBackupDate(date: java.time.ZonedDateTime) = backup.setLastBackupDate(
        date
    )

    val lastBackupFailure get() = backup.lastBackupFailure

    suspend fun setLastBackupFailure(reason: String) = backup.setLastBackupFailure(reason)

    suspend fun clearLastBackupFailure() = backup.clearLastBackupFailure()

    companion object {
        fun dateFormat(format: String): DateTimeFormatter = when (format) {
            "" ->
                DateTimeFormatter.ofPattern(
                    DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                        FormatStyle.SHORT,
                        null,
                        IsoChronology.INSTANCE,
                        Locale.getDefault()
                    )
                )
            else -> DateTimeFormatter.ofPattern(format, Locale.getDefault())
        }
    }
}
