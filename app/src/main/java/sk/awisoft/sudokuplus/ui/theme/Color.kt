package sk.awisoft.sudokuplus.ui.theme

import androidx.annotation.FloatRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils

object SudokuPlusColors {
    // Base colors
    val SoftCream = Color(0xFFF8F5E6)
    val DeepestSlate = Color(0xFF1E2532)
    val DeepSlateBlue = Color(0xFF3C4A63)
    val MutedTeal = Color(0xFF5B9EA6)
    val WarmCoral = Color(0xFFD4A59A)
    val WarmCoralLight = Color(0xFFE8C4BC)

    // Extended palette for M3 color scheme
    val SoftCreamDark = Color(0xFFE8E5D6)
    val DeepSlateLight = Color(0xFF2A3444)
    val MutedTealDark = Color(0xFF4A8A91)
    val MutedTealLight = Color(0xFF7AB4BB)

    // Surface variants
    val SurfaceContainerLight = Color(0xFFF0EDE0)
    val SurfaceContainerLowLight = Color(0xFFF4F1E3)
    val SurfaceContainerHighLight = Color(0xFFE8E5D8)
    val SurfaceContainerHighestLight = Color(0xFFE0DDD0)

    val SurfaceContainerDark = Color(0xFF262F3C)
    val SurfaceContainerLowDark = Color(0xFF222A37)
    val SurfaceContainerHighDark = Color(0xFF2E3846)
    val SurfaceContainerHighestDark = Color(0xFF364251)

    // Error colors harmonized with palette
    val ErrorLight = Color(0xFFBA1A1A)
    val ErrorDark = Color(0xFFFFB4AB)
    val OnErrorLight = Color(0xFFFFFFFF)
    val OnErrorDark = Color(0xFF690005)
    val ErrorContainerLight = Color(0xFFFFDAD6)
    val ErrorContainerDark = Color(0xFF93000A)
}
object ColorUtils {

    fun Color.blend(
        color: Color,
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
    ): Color = ColorUtils.blendARGB(this.toArgb(), color.toArgb(), fraction).toColor()

    fun Color.darken(
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
    ): Color = blend(color = Color.Black, fraction = fraction)

    fun Color.lighten(
        @FloatRange(from = 0.0, to = 1.0) fraction: Float = 0.2f
    ): Color = blend(color = Color.White, fraction = fraction)

    fun Int.toColor(): Color = Color(color = this)

    @Composable
    fun Color.harmonizeWithPrimary(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) fraction: Float = 0.2f
    ): Color = blend(MaterialTheme.colorScheme.primary, fraction)

}