package sk.awisoft.sudokuplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import sk.awisoft.sudokuplus.ui.theme.ColorUtils.harmonizeWithPrimary

object BoardColors {
    // "Given" numbers - Deep Slate Blue (light) / Soft Cream (dark)
    @Composable
    fun foregroundColor(isDark: Boolean = isSystemInDarkTheme()): Color =
        if (isDark) SudokuPlusColors.SoftCream else SudokuPlusColors.DeepSlateBlue

    // Notes color - slightly muted version of foreground
    @Composable
    fun notesColor(isDark: Boolean = isSystemInDarkTheme()): Color =
        if (isDark) SudokuPlusColors.SoftCreamDark.copy(alpha = 0.75f)
        else SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.7f)

    // User-entered numbers - Muted Teal (both modes)
    @Composable
    fun altForegroundColor(): Color = SudokuPlusColors.MutedTeal

    // Error color - harmonized red
    @Composable
    fun errorColor(): Color = Color(230, 67, 83).harmonizeWithPrimary()

    // Selected cell highlight - Warm Coral (both modes)
    @Composable
    fun highlightColor(isDark: Boolean = isSystemInDarkTheme()): Color =
        if (isDark) SudokuPlusColors.SurfaceContainerLight.copy(alpha = 0.4f)
        else SudokuPlusColors.MutedTeal.copy(alpha = 0.35f)

    // Thick grid lines (3x3 box separators) - Deep Slate Blue (light) / Soft Cream (dark)
    @Composable
    fun thickLineColor(isDark: Boolean = isSystemInDarkTheme()): Color =
        if (isDark) SudokuPlusColors.SoftCream.copy(alpha = 0.6f)
        else SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.7f)

    // Thin grid lines (cell separators)
    @Composable
    fun thinLineColor(isDark: Boolean = isSystemInDarkTheme()): Color =
        if (isDark) SudokuPlusColors.SoftCream.copy(alpha = 0.25f)
        else SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.3f)
}

interface SudokuBoardColors {
    val foregroundColor: Color
    val notesColor: Color
    val altForegroundColor: Color
    val errorColor: Color
    val highlightColor: Color
    val thickLineColor: Color
    val thinLineColor: Color
}

class SudokuBoardColorsImpl(
    override val foregroundColor: Color = Color.White,
    override val notesColor: Color = Color.White,
    override val altForegroundColor: Color = Color.White,
    override val errorColor: Color = Color.White,
    override val highlightColor: Color = Color.White,
    override val thickLineColor: Color = Color.White,
    override val thinLineColor: Color = Color.White,
) : SudokuBoardColors
