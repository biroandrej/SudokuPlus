package sk.awisoft.sudokuplus.ui.seasonal.celebrations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import sk.awisoft.sudokuplus.core.seasonal.model.EventType

private data class ThemedParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float,
    val shape: ParticleShape
)

private enum class ParticleShape {
    CIRCLE,
    STAR,
    DIAMOND,
    EGG,
    LEAF,
    PUMPKIN,
    SNOWFLAKE,
    FIREWORK
}

@Composable
fun ThemedCelebrationEffect(
    eventType: EventType,
    modifier: Modifier = Modifier,
    particleCount: Int = 60,
    durationMillis: Int = 3000,
    onComplete: () -> Unit = {}
) {
    val config = remember(eventType) { getEventConfig(eventType) }
    val progress = remember { Animatable(0f) }

    val particles = remember(eventType) {
        List(particleCount) {
            val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
            val speed = Random.nextFloat() * 8f + 4f
            ThemedParticle(
                x = 0.5f,
                y = 0.1f,
                velocityX = cos(angle) * speed * 0.6f,
                velocityY = sin(angle) * speed * 0.4f + 1.5f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 8f - 4f,
                color = config.colors.random(),
                size = Random.nextFloat() * 14f + 8f,
                shape = config.shapes.random()
            )
        }
    }

    LaunchedEffect(eventType) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 0.12f
        val currentProgress = progress.value

        particles.forEach { particle ->
            val time = currentProgress * durationMillis / 16f
            val x = size.width * particle.x + particle.velocityX * time
            val y = particle.y * size.height + particle.velocityY * time +
                0.5f * gravity * time * time
            val alpha = (1f - currentProgress * 0.8f).coerceIn(0f, 1f)

            if (y in -particle.size..size.height + particle.size &&
                x in -particle.size..size.width + particle.size
            ) {
                val rotation = particle.rotation + particle.rotationSpeed * time
                val color = particle.color.copy(alpha = alpha)
                val s = particle.size

                rotate(rotation, pivot = Offset(x, y)) {
                    when (particle.shape) {
                        ParticleShape.CIRCLE -> drawCircle(color, s / 2, Offset(x, y))
                        ParticleShape.STAR -> drawStar(color, x, y, s)
                        ParticleShape.DIAMOND -> drawDiamond(color, x, y, s)
                        ParticleShape.EGG -> drawEgg(color, x, y, s)
                        ParticleShape.LEAF -> drawLeaf(color, x, y, s)
                        ParticleShape.PUMPKIN -> drawPumpkin(color, x, y, s)
                        ParticleShape.SNOWFLAKE -> drawSnowflake(color, x, y, s)
                        ParticleShape.FIREWORK -> drawFirework(color, x, y, s)
                    }
                }
            }
        }
    }
}

private data class EventParticleConfig(
    val colors: List<Color>,
    val shapes: List<ParticleShape>
)

private fun getEventConfig(eventType: EventType): EventParticleConfig = when (eventType) {
    EventType.EASTER -> EventParticleConfig(
        colors = listOf(
            Color(0xFFE8F5E9),
            Color(0xFFFFF9C4),
            Color(0xFFF8BBD0),
            Color(0xFFB3E5FC),
            Color(0xFFD1C4E9),
            Color(0xFF8BC34A)
        ),
        shapes = listOf(
            ParticleShape.EGG,
            ParticleShape.EGG,
            ParticleShape.CIRCLE,
            ParticleShape.LEAF
        )
    )
    EventType.SUMMER -> EventParticleConfig(
        colors = listOf(
            Color(0xFFFFB74D),
            Color(0xFF4FC3F7),
            Color(0xFFFFF176),
            Color(0xFF81C784),
            Color(0xFFFF8A65),
            Color(0xFF03A9F4)
        ),
        shapes = listOf(ParticleShape.STAR, ParticleShape.CIRCLE, ParticleShape.DIAMOND)
    )
    EventType.HALLOWEEN -> EventParticleConfig(
        colors = listOf(
            Color(0xFFFF9800),
            Color(0xFF9C27B0),
            Color(0xFFFF5722),
            Color(0xFF4CAF50),
            Color(0xFFFFEB3B),
            Color(0xFFE040FB)
        ),
        shapes = listOf(ParticleShape.PUMPKIN, ParticleShape.STAR, ParticleShape.DIAMOND)
    )
    EventType.CHRISTMAS -> EventParticleConfig(
        colors = listOf(
            Color(0xFFC62828),
            Color(0xFF2E7D32),
            Color(0xFFFFD600),
            Color(0xFFFFFFFF),
            Color(0xFFE57373),
            Color(0xFF81C784)
        ),
        shapes = listOf(ParticleShape.SNOWFLAKE, ParticleShape.STAR, ParticleShape.CIRCLE)
    )
    EventType.NEW_YEAR -> EventParticleConfig(
        colors = listOf(
            Color(0xFFFFD700),
            Color(0xFFC0C0C0),
            Color(0xFF1565C0),
            Color(0xFFFF4081),
            Color(0xFF7C4DFF),
            Color(0xFF00E5FF)
        ),
        shapes = listOf(ParticleShape.FIREWORK, ParticleShape.STAR, ParticleShape.DIAMOND)
    )
}

private fun DrawScope.drawStar(color: Color, cx: Float, cy: Float, size: Float) {
    val path = Path()
    val outerR = size / 2
    val innerR = size / 5
    for (i in 0 until 5) {
        val outerAngle = (i * 72 - 90) * Math.PI.toFloat() / 180
        val innerAngle = ((i * 72) + 36 - 90) * Math.PI.toFloat() / 180
        val ox = cx + cos(outerAngle) * outerR
        val oy = cy + sin(outerAngle) * outerR
        val ix = cx + cos(innerAngle) * innerR
        val iy = cy + sin(innerAngle) * innerR
        if (i == 0) path.moveTo(ox, oy) else path.lineTo(ox, oy)
        path.lineTo(ix, iy)
    }
    path.close()
    drawPath(path, color)
}

private fun DrawScope.drawDiamond(color: Color, cx: Float, cy: Float, size: Float) {
    val half = size / 2
    val path = Path().apply {
        moveTo(cx, cy - half)
        lineTo(cx + half * 0.6f, cy)
        lineTo(cx, cy + half)
        lineTo(cx - half * 0.6f, cy)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawEgg(color: Color, cx: Float, cy: Float, size: Float) {
    drawOval(color, Offset(cx - size * 0.3f, cy - size * 0.4f), Size(size * 0.6f, size * 0.8f))
}

private fun DrawScope.drawLeaf(color: Color, cx: Float, cy: Float, size: Float) {
    val path = Path().apply {
        moveTo(cx, cy - size / 2)
        quadraticTo(cx + size / 2, cy, cx, cy + size / 2)
        quadraticTo(cx - size / 2, cy, cx, cy - size / 2)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawPumpkin(color: Color, cx: Float, cy: Float, size: Float) {
    drawOval(color, Offset(cx - size * 0.4f, cy - size * 0.35f), Size(size * 0.8f, size * 0.7f))
    drawRect(
        Color(0xFF4CAF50),
        Offset(cx - size * 0.05f, cy - size * 0.5f),
        Size(size * 0.1f, size * 0.2f)
    )
}

private fun DrawScope.drawSnowflake(color: Color, cx: Float, cy: Float, size: Float) {
    val r = size / 2
    for (i in 0 until 3) {
        val angle = i * 60f * Math.PI.toFloat() / 180f
        drawLine(
            color,
            Offset(cx - cos(angle) * r, cy - sin(angle) * r),
            Offset(cx + cos(angle) * r, cy + sin(angle) * r),
            strokeWidth = 2f
        )
    }
    drawCircle(color, r * 0.25f, Offset(cx, cy))
}

private fun DrawScope.drawFirework(color: Color, cx: Float, cy: Float, size: Float) {
    val r = size / 2
    for (i in 0 until 8) {
        val angle = i * 45f * Math.PI.toFloat() / 180f
        drawLine(
            color,
            Offset(cx, cy),
            Offset(cx + cos(angle) * r, cy + sin(angle) * r),
            strokeWidth = 2.5f
        )
        drawCircle(color, 2f, Offset(cx + cos(angle) * r, cy + sin(angle) * r))
    }
}
