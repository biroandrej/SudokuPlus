package sk.awisoft.sudokuplus.ui.app_crash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager

@HiltViewModel
class CrashViewModel
@Inject
constructor(
    themeSettingsManager: ThemeSettingsManager,
    appSettingsManager: AppSettingsManager
) : ViewModel() {
    val dc = themeSettingsManager.dynamicColors
    val darkTheme = themeSettingsManager.darkTheme
    val amoledBlack = themeSettingsManager.amoledBlack
    val firstLaunch = appSettingsManager.firstLaunch
    val monetSudokuBoard = themeSettingsManager.monetSudokuBoard
}
