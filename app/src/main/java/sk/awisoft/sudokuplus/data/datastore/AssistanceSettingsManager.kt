package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.core.qqwing.advanced_hint.AdvancedHintSettings
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssistanceSettingsManager @Inject constructor(
    settingsDataStore: SettingsDataStore
) {
    private val dataStore = settingsDataStore.dataStore

    private val highlightMistakesKey = intPreferencesKey("mistakes_highlight")
    private val highlightIdenticalKey = booleanPreferencesKey("same_values_highlight")
    private val remainingUseKey = booleanPreferencesKey("remaining_use")
    private val positionLinesKey = booleanPreferencesKey("position_lines")
    private val autoEraseNotesKey = booleanPreferencesKey("notes_auto_erase")
    private val advancedHintKey = booleanPreferencesKey("advanced_hint")
    private val ahFullHouseKey = booleanPreferencesKey("ah_full_house")
    private val ahNakedSingle = booleanPreferencesKey("ah_naked_single")
    private val ahHiddenSingle = booleanPreferencesKey("ah_hidden_single")
    private val ahCheckWrongValue = booleanPreferencesKey("ah_check_wrong_value")

    // Highlight mistakes
    val highlightMistakes = dataStore.data.map { preferences ->
        preferences[highlightMistakesKey] ?: PreferencesConstants.DEFAULT_HIGHLIGHT_MISTAKES
    }

    suspend fun setHighlightMistakes(value: Int) {
        dataStore.edit { settings ->
            settings[highlightMistakesKey] = value
        }
    }

    // Highlight identical values
    val highlightIdentical = dataStore.data.map { preferences ->
        preferences[highlightIdenticalKey] ?: PreferencesConstants.DEFAULT_HIGHLIGHT_IDENTICAL
    }

    suspend fun setSameValuesHighlight(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[highlightIdenticalKey] = enabled
        }
    }

    // Remaining uses
    val remainingUse = dataStore.data.map { preferences ->
        preferences[remainingUseKey] ?: PreferencesConstants.DEFAULT_REMAINING_USES
    }

    suspend fun setRemainingUse(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[remainingUseKey] = enabled
        }
    }

    // Position lines
    val positionLines = dataStore.data.map { preferences ->
        preferences[positionLinesKey] ?: PreferencesConstants.DEFAULT_POSITION_LINES
    }

    suspend fun setPositionLines(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[positionLinesKey] = enabled
        }
    }

    // Auto erase notes
    val autoEraseNotes = dataStore.data.map { preferences ->
        preferences[autoEraseNotesKey] ?: PreferencesConstants.DEFAULT_AUTO_ERASE_NOTES
    }

    suspend fun setAutoEraseNotes(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[autoEraseNotesKey] = enabled
        }
    }

    // Advanced hint
    val advancedHintEnabled = dataStore.data.map { settings ->
        settings[advancedHintKey] ?: PreferencesConstants.DEFAULT_ADVANCED_HINT
    }

    suspend fun setAdvancedHint(enabled: Boolean) {
        dataStore.edit { settings ->
            settings[advancedHintKey] = enabled
        }
    }

    val advancedHintSettings = dataStore.data.map { settings ->
        AdvancedHintSettings(
            fullHouse = settings[ahFullHouseKey] ?: true,
            nakedSingle = settings[ahNakedSingle] ?: true,
            hiddenSingle = settings[ahHiddenSingle] ?: true,
            checkWrongValue = settings[ahCheckWrongValue] ?: true
        )
    }

    suspend fun updateAdvancedHintSettings(ahSettings: AdvancedHintSettings) {
        dataStore.edit { settings ->
            settings[ahFullHouseKey] = ahSettings.fullHouse
            settings[ahNakedSingle] = ahSettings.nakedSingle
            settings[ahHiddenSingle] = ahSettings.hiddenSingle
            settings[ahCheckWrongValue] = ahSettings.checkWrongValue
        }
    }
}
