package sk.awisoft.sudokuplus.ui.seasonal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType

data class SeasonalColors(
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val accent: Color
) {
    companion object {
        val Default = SeasonalColors(
            primary = Color.Unspecified,
            secondary = Color.Unspecified,
            background = Color.Unspecified,
            accent = Color.Unspecified
        )

        fun fromEventTheme(theme: EventTheme) = SeasonalColors(
            primary = Color(theme.primaryColor),
            secondary = Color(theme.secondaryColor),
            background = Color(theme.backgroundColor),
            accent = Color(theme.accentColor)
        )

        fun forEventType(eventType: EventType) = when (eventType) {
            EventType.EASTER -> SeasonalColors(
                primary = Color(0xFF8BC34A),
                secondary = Color(0xFFFFEB3B),
                background = Color(0xFFF1F8E9),
                accent = Color(0xFF689F38)
            )
            EventType.SUMMER -> SeasonalColors(
                primary = Color(0xFFFF9800),
                secondary = Color(0xFF03A9F4),
                background = Color(0xFFFFF3E0),
                accent = Color(0xFFE65100)
            )
            EventType.HALLOWEEN -> SeasonalColors(
                primary = Color(0xFFFF5722),
                secondary = Color(0xFF9C27B0),
                background = Color(0xFF212121),
                accent = Color(0xFFFF9800)
            )
            EventType.CHRISTMAS -> SeasonalColors(
                primary = Color(0xFFC62828),
                secondary = Color(0xFF2E7D32),
                background = Color(0xFFFFEBEE),
                accent = Color(0xFFB71C1C)
            )
            EventType.NEW_YEAR -> SeasonalColors(
                primary = Color(0xFF1565C0),
                secondary = Color(0xFFFFD600),
                background = Color(0xFFE3F2FD),
                accent = Color(0xFF0D47A1)
            )
        }
    }
}

val LocalSeasonalColors = staticCompositionLocalOf { SeasonalColors.Default }

@Composable
fun SeasonalEventTheme(
    eventType: EventType?,
    eventTheme: EventTheme? = null,
    content: @Composable () -> Unit
) {
    val colors = when {
        eventTheme != null -> SeasonalColors.fromEventTheme(eventTheme)
        eventType != null -> SeasonalColors.forEventType(eventType)
        else -> SeasonalColors.Default
    }

    CompositionLocalProvider(LocalSeasonalColors provides colors) {
        content()
    }
}
