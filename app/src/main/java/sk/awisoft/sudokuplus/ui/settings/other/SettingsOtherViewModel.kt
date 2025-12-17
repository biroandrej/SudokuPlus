package sk.awisoft.sudokuplus.ui.settings.other

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.awisoft.sudokuplus.data.database.AppDatabase
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.TipCardsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsOtherViewModel @Inject constructor(
    private val settings: AppSettingsManager,
    private val tipCardsDataStore: TipCardsDataStore,
    private val appDatabase: AppDatabase
) : ViewModel() {
    val saveLastSelectedDifficultyType = settings.saveSelectedGameDifficultyType
    fun updateSaveLastSelectedDifficultyType(enabled: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            settings.setSaveSelectedGameDifficultyType(enabled)
        }

    val keepScreenOn = settings.keepScreenOn
    fun updateKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setKeepScreenOn(enabled)
        }
    }

    fun resetTipCards() {
        viewModelScope.launch {
            tipCardsDataStore.setStreakCard(true)
            tipCardsDataStore.setRecordCard(true)
        }
    }

    fun deleteAllTables() {
        viewModelScope.launch(Dispatchers.IO) {
            appDatabase.clearAllTables()
        }
    }
}
