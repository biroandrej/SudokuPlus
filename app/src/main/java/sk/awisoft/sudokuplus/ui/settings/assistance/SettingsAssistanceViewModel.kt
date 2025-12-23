package sk.awisoft.sudokuplus.ui.settings.assistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager

@HiltViewModel
class SettingsAssistanceViewModel
@Inject
constructor(
    private val settings: AppSettingsManager
) : ViewModel() {
    val remainingUse = settings.remainingUse

    fun updateRemainingUse(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setRemainingUse(enabled)
        }
    }

    val highlightIdentical = settings.highlightIdentical

    fun updateHighlightIdentical(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        settings.setSameValuesHighlight(enabled)
    }

    val autoEraseNotes = settings.autoEraseNotes

    fun updateAutoEraseNotes(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setAutoEraseNotes(enabled)
        }
    }

    val highlightMistakes = settings.highlightMistakes

    fun updateMistakesHighlight(index: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setHighlightMistakes(index)
        }
    }
}
