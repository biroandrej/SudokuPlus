package sk.awisoft.sudokuplus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import sk.awisoft.sudokuplus.ui.theme.ColorUtils.harmonizeWithPrimary

object BoardColors {
    // "Given" numbers - Deep Slate Blue (light) / Soft Cream (dark)
    inline val foregroundColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) {
            SudokuPlusColors.SoftCream
        } else {
            SudokuPlusColors.DeepSlateBlue
        }

    // Notes color - slightly muted version of foreground
    inline val notesColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) {
            SudokuPlusColors.SoftCreamDark.copy(alpha = 0.75f)
        } else {
            SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.7f)
        }

    // User-entered numbers - Muted Teal (both modes)
    inline val altForegroundColor: Color
        @Composable
        get() = SudokuPlusColors.MutedTeal

    // Error color - harmonized red
    inline val errorColor: Color
        @Composable
        get() = Color(230, 67, 83).harmonizeWithPrimary()

    // Selected cell highlight - Warm Coral (both modes)
    inline val highlightColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) {
            SudokuPlusColors.WarmCoralLight.copy(alpha = 0.4f)
        } else {
            SudokuPlusColors.WarmCoral.copy(alpha = 0.35f)
        }

    // Thick grid lines (3x3 box separators) - Deep Slate Blue (light) / Soft Cream (dark)
    inline val thickLineColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) {
            SudokuPlusColors.SoftCream.copy(alpha = 0.6f)
        } else {
            SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.7f)
        }

    // Thin grid lines (cell separators)
    inline val thinLineColor: Color
        @Composable
        get() = if (isSystemInDarkTheme()) {
            SudokuPlusColors.SoftCream.copy(alpha = 0.25f)
        } else {
            SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.3f)
        }
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
