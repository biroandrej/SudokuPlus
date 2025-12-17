package sk.awisoft.sudokuplus.ui.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsAppearanceViewModel @Inject constructor(
    private val themeSettings: ThemeSettingsManager,
    private val settings: AppSettingsManager
) : ViewModel() {
    val darkTheme by lazy {
        themeSettings.darkTheme
    }

    fun updateDarkTheme(value: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            themeSettings.setDarkTheme(value)
        }

    val dynamicColors by lazy {
        themeSettings.dynamicColors
    }

    fun updateDynamicColors(enabled: Boolean) =
        viewModelScope.launch {
            themeSettings.setDynamicColors(enabled)
        }

    val amoledBlack by lazy {
        themeSettings.amoledBlack
    }

    fun updateAmoledBlack(enabled: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            themeSettings.setAmoledBlack(enabled)
        }

    val dateFormat = settings.dateFormat
    fun updateDateFormat(format: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settings.setDateFormat(format)
        }
    }

    fun checkCustomDateFormat(pattern: String): Boolean {
        return try {
            DateTimeFormatter.ofPattern(pattern)
            true
        } catch (e: Exception) {
            false
        }
    }
}
