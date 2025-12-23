package sk.awisoft.sudokuplus.ui.theme

import android.os.Build
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.rememberDynamicMaterialThemeState
import sk.awisoft.sudokuplus.core.PreferencesConstants

private val SudokuPlusLightColorScheme =
    lightColorScheme(
        primary = SudokuPlusColors.MutedTeal,
        onPrimary = SudokuPlusColors.SoftCream,
        primaryContainer = SudokuPlusColors.MutedTealLight,
        onPrimaryContainer = SudokuPlusColors.DeepestSlate,
        secondary = SudokuPlusColors.DeepSlateBlue,
        onSecondary = SudokuPlusColors.SoftCream,
        secondaryContainer = SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.15f),
        onSecondaryContainer = SudokuPlusColors.DeepSlateBlue,
        tertiary = SudokuPlusColors.WarmCoral,
        onTertiary = SudokuPlusColors.DeepestSlate,
        tertiaryContainer = SudokuPlusColors.WarmCoralLight,
        onTertiaryContainer = SudokuPlusColors.DeepestSlate,
        background = SudokuPlusColors.SoftCream,
        onBackground = SudokuPlusColors.DeepSlateBlue,
        surface = SudokuPlusColors.SoftCream,
        onSurface = SudokuPlusColors.DeepSlateBlue,
        surfaceVariant = SudokuPlusColors.SurfaceContainerHighLight,
        onSurfaceVariant = SudokuPlusColors.DeepSlateBlue,
        surfaceContainer = SudokuPlusColors.SurfaceContainerLight,
        surfaceContainerLow = SudokuPlusColors.SurfaceContainerLowLight,
        surfaceContainerHigh = SudokuPlusColors.SurfaceContainerHighLight,
        surfaceContainerHighest = SudokuPlusColors.SurfaceContainerHighestLight,
        surfaceContainerLowest = SudokuPlusColors.SoftCream,
        outline = SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.5f),
        outlineVariant = SudokuPlusColors.DeepSlateBlue.copy(alpha = 0.25f),
        error = SudokuPlusColors.ErrorLight,
        onError = SudokuPlusColors.OnErrorLight,
        errorContainer = SudokuPlusColors.ErrorContainerLight,
        onErrorContainer = SudokuPlusColors.ErrorLight,
        inverseSurface = SudokuPlusColors.DeepestSlate,
        inverseOnSurface = SudokuPlusColors.SoftCream,
        inversePrimary = SudokuPlusColors.MutedTealLight,
        scrim = SudokuPlusColors.DeepestSlate
    )

// Custom LibreSudoku Dark Color Scheme
private val SudokuPlusDarkColorScheme =
    darkColorScheme(
        primary = SudokuPlusColors.MutedTeal,
        onPrimary = SudokuPlusColors.DeepestSlate,
        primaryContainer = SudokuPlusColors.MutedTealDark,
        onPrimaryContainer = SudokuPlusColors.SoftCream,
        secondary = SudokuPlusColors.SoftCreamDark,
        onSecondary = SudokuPlusColors.DeepestSlate,
        secondaryContainer = SudokuPlusColors.DeepSlateLight,
        onSecondaryContainer = SudokuPlusColors.SoftCream,
        tertiary = SudokuPlusColors.WarmCoralLight,
        onTertiary = SudokuPlusColors.DeepestSlate,
        tertiaryContainer = SudokuPlusColors.WarmCoral.copy(alpha = 0.3f),
        onTertiaryContainer = SudokuPlusColors.WarmCoralLight,
        background = SudokuPlusColors.DeepestSlate,
        onBackground = SudokuPlusColors.SoftCream,
        surface = SudokuPlusColors.DeepestSlate,
        onSurface = SudokuPlusColors.SoftCream,
        surfaceVariant = SudokuPlusColors.SurfaceContainerHighDark,
        onSurfaceVariant = SudokuPlusColors.SoftCreamDark,
        surfaceContainer = SudokuPlusColors.SurfaceContainerDark,
        surfaceContainerLow = SudokuPlusColors.SurfaceContainerLowDark,
        surfaceContainerHigh = SudokuPlusColors.SurfaceContainerHighDark,
        surfaceContainerHighest = SudokuPlusColors.SurfaceContainerHighestDark,
        surfaceContainerLowest = SudokuPlusColors.DeepestSlate,
        outline = SudokuPlusColors.SoftCream.copy(alpha = 0.5f),
        outlineVariant = SudokuPlusColors.SoftCream.copy(alpha = 0.25f),
        error = SudokuPlusColors.ErrorDark,
        onError = SudokuPlusColors.OnErrorDark,
        errorContainer = SudokuPlusColors.ErrorContainerDark,
        onErrorContainer = SudokuPlusColors.ErrorDark,
        inverseSurface = SudokuPlusColors.SoftCream,
        inverseOnSurface = SudokuPlusColors.DeepestSlate,
        inversePrimary = SudokuPlusColors.MutedTealDark,
        scrim = Color.Black
    )

// AMOLED Dark Color Scheme
private val LibreSudokuAmoledColorScheme =
    SudokuPlusDarkColorScheme.copy(
        background = Color.Black,
        surface = Color.Black,
        surfaceContainerLowest = Color.Black
    )

@Composable
fun SudokuPlusTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    amoled: Boolean = false,
    colorSeed: Color = Color(PreferencesConstants.DEFAULT_THEME_SEED_COLOR),
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    content: @Composable () -> Unit
) {
    var colorScheme: ColorScheme =
        when {
            darkTheme && amoled -> LibreSudokuAmoledColorScheme
            darkTheme -> SudokuPlusDarkColorScheme
            else -> SudokuPlusLightColorScheme
        }

    // Override with dynamic color if enabled and available
    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        colorScheme =
            when {
                darkTheme && amoled ->
                    dynamicDarkColorScheme(context).copy(
                        background = Color.Black,
                        surface = Color.Black
                    )
                darkTheme && !amoled -> dynamicDarkColorScheme(context)
                else -> dynamicLightColorScheme(context)
            }
    }

    val materialThemeState =
        rememberDynamicMaterialThemeState(
            seedColor = colorSeed,
            isDark = darkTheme,
            style = paletteStyle,
            isAmoled = amoled,
            modifyColorScheme = { colorScheme }
        )

    DynamicMaterialTheme(
        state = materialThemeState,
        animate = true,
        animationSpec =
        tween(
            durationMillis = 200,
            easing = FastOutSlowInEasing
        ),
        typography = Typography,
        content = content
    )
}
