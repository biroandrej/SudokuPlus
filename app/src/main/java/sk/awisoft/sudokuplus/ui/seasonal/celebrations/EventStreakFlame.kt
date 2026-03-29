package sk.awisoft.sudokuplus.ui.seasonal.celebrations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import kotlin.math.sin

@Composable
fun EventStreakFlame(streakDays: Int, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "flame")
    val flicker by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flicker"
    )

    val intensity = (streakDays.coerceAtMost(10) / 10f).coerceAtLeast(0.3f)

    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val flickerOffset = sin(flicker * Math.PI.toFloat()) * h * 0.08f

        val flamePath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f - flickerOffset)
            cubicTo(
                w * 0.2f,
                h * 0.35f,
                w * 0.1f,
                h * 0.7f,
                w * 0.3f,
                h * 0.95f
            )
            quadraticTo(w * 0.5f, h * 0.8f, w * 0.7f, h * 0.95f)
            cubicTo(
                w * 0.9f,
                h * 0.7f,
                w * 0.8f,
                h * 0.35f,
                w * 0.5f,
                h * 0.05f - flickerOffset
            )
            close()
        }

        drawPath(
            flamePath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = intensity),
                    Color(0xFFFF9800).copy(alpha = intensity),
                    Color(0xFFF44336).copy(alpha = intensity * 0.8f)
                )
            )
        )

        // Inner flame
        val innerPath = Path().apply {
            moveTo(w * 0.5f, h * 0.25f - flickerOffset * 0.5f)
            cubicTo(
                w * 0.35f,
                h * 0.45f,
                w * 0.3f,
                h * 0.65f,
                w * 0.4f,
                h * 0.85f
            )
            quadraticTo(w * 0.5f, h * 0.75f, w * 0.6f, h * 0.85f)
            cubicTo(
                w * 0.7f,
                h * 0.65f,
                w * 0.65f,
                h * 0.45f,
                w * 0.5f,
                h * 0.25f - flickerOffset * 0.5f
            )
            close()
        }

        drawPath(
            innerPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFF9C4).copy(alpha = intensity),
                    Color(0xFFFFE082).copy(alpha = intensity * 0.9f)
                ),
                startY = h * 0.25f,
                endY = h * 0.85f
            )
        )
    }
}
