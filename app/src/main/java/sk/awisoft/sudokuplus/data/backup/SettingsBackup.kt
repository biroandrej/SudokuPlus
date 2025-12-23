package sk.awisoft.sudokuplus.data.backup

import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager

@Serializable
data class SettingsBackup(
    val inputMethod: Int = PreferencesConstants.DEFAULT_INPUT_METHOD,
    val mistakesLimit: Boolean = PreferencesConstants.DEFAULT_MISTAKES_LIMIT,
    val hintDisabled: Boolean = PreferencesConstants.DEFAULT_HINTS_DISABLED,
    val timer: Boolean = PreferencesConstants.DEFAULT_SHOW_TIMER,
    val resetTimer: Boolean = PreferencesConstants.DEFAULT_GAME_RESET_TIMER,
    val highlightMistakes: Int = PreferencesConstants.DEFAULT_HIGHLIGHT_MISTAKES,
    val highlightIdentical: Boolean = PreferencesConstants.DEFAULT_HIGHLIGHT_IDENTICAL,
    val remainingUses: Boolean = PreferencesConstants.DEFAULT_REMAINING_USES,
    val positionLines: Boolean = PreferencesConstants.DEFAULT_POSITION_LINES,
    val autoEraseNotes: Boolean = PreferencesConstants.DEFAULT_AUTO_ERASE_NOTES,
    val fontSize: Int = PreferencesConstants.DEFAULT_FONT_SIZE_FACTOR,
    val keepScreenOn: Boolean = PreferencesConstants.DEFAULT_KEEP_SCREEN_ON,
    val funKeyboardOverNum: Boolean = PreferencesConstants.DEFAULT_FUN_KEYBOARD_OVER_NUM,
    val dateFormat: String = "",
    val saveSelectedGameDifficulty: Boolean = PreferencesConstants.DEFAULT_SAVE_LAST_SELECTED_DIFF_TYPE,
    val autoBackupInterval: Long = PreferencesConstants.DEFAULT_AUTOBACKUP_INTERVAL,
    val maxAutoBackups: Int = PreferencesConstants.DEFAULT_AUTO_BACKUPS_NUMBER,
    val dynamicColors: Boolean = PreferencesConstants.DEFAULT_DYNAMIC_COLORS,
    val darkTheme: Int = PreferencesConstants.DEFAULT_DARK_THEME,
    val monetSudokuBoard: Boolean = PreferencesConstants.DEFAULT_MONET_SUDOKU_BOARD,
    val boardCrossHighlight: Boolean = PreferencesConstants.DEFAULT_BOARD_CROSS_HIGHLIGHT,
    val advancedHint: Boolean = PreferencesConstants.DEFAULT_ADVANCED_HINT
) {
    suspend fun setSettings(settings: AppSettingsManager, themeSettings: ThemeSettingsManager) {
        settings.setInputMethod(inputMethod)
        settings.setMistakesLimit(mistakesLimit)
        settings.setHintsDisabled(hintDisabled)
        settings.setTimer(timer)
        settings.setResetTimer(resetTimer)
        settings.setHighlightMistakes(highlightMistakes)
        settings.setSameValuesHighlight(highlightIdentical)
        settings.setRemainingUse(remainingUses)
        settings.setPositionLines(positionLines)
        settings.setAutoEraseNotes(autoEraseNotes)
        settings.setFontSize(fontSize)
        settings.setKeepScreenOn(keepScreenOn)
        settings.setFunKeyboardOverNum(funKeyboardOverNum)
        settings.setDateFormat(dateFormat)
        settings.setSaveSelectedGameDifficultyType(saveSelectedGameDifficulty)
        settings.setAutoBackupInterval(autoBackupInterval)
        settings.setAutoBackupsNumber(maxAutoBackups)
        settings.setAdvancedHint(advancedHint)

        themeSettings.setDynamicColors(dynamicColors)
        themeSettings.setDarkTheme(darkTheme)
        themeSettings.setMonetSudokuBoard(monetSudokuBoard)
        themeSettings.setBoardCrossHighlight(boardCrossHighlight)
    }

    companion object {
        suspend fun getSettings(
            settings: AppSettingsManager,
            themeSettings: ThemeSettingsManager
        ): SettingsBackup {
            return SettingsBackup(
                inputMethod = settings.inputMethod.first(),
                mistakesLimit = settings.mistakesLimit.first(),
                hintDisabled = settings.hintsDisabled.first(),
                timer = settings.timerEnabled.first(),
                resetTimer = settings.resetTimerEnabled.first(),
                highlightMistakes = settings.highlightMistakes.first(),
                highlightIdentical = settings.highlightIdentical.first(),
                remainingUses = settings.remainingUse.first(),
                positionLines = settings.positionLines.first(),
                autoEraseNotes = settings.autoEraseNotes.first(),
                fontSize = settings.fontSize.first(),
                keepScreenOn = settings.keepScreenOn.first(),
                funKeyboardOverNum = settings.funKeyboardOverNumbers.first(),
                dateFormat = settings.dateFormat.first(),
                saveSelectedGameDifficulty = settings.saveSelectedGameDifficultyType.first(),
                autoBackupInterval = settings.autoBackupInterval.first(),
                maxAutoBackups = settings.autoBackupsNumber.first(),
                dynamicColors = themeSettings.dynamicColors.first(),
                darkTheme = themeSettings.darkTheme.first(),
                monetSudokuBoard = themeSettings.monetSudokuBoard.first(),
                boardCrossHighlight = themeSettings.boardCrossHighlight.first(),
                advancedHint = settings.advancedHintEnabled.first()
            )
        }
    }
}
