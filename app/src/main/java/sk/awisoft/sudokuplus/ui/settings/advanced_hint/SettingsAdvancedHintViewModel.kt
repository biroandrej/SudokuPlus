package sk.awisoft.sudokuplus.ui.settings.advanced_hint

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.awisoft.sudokuplus.core.qqwing.advanced_hint.AdvancedHintSettings
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsAdvancedHintViewModel @Inject constructor(
    private val settingsManager: AppSettingsManager
) : ViewModel() {
    val advancedHintEnabled = settingsManager.advancedHintEnabled
    val advancedHintSettings = settingsManager.advancedHintSettings

    fun setAdvancedHintEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setAdvancedHint(enabled)
        }
    }

    fun updateAdvancedHintSettings(settings: AdvancedHintSettings) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.updateAdvancedHintSettings(settings)
        }
    }
}
