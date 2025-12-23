package sk.awisoft.sudokuplus.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val color: Color,
    val size: Float
)

@Composable
fun ConfettiEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    durationMillis: Int = 3000,
    onComplete: () -> Unit = {}
) {
    val colors =
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary,
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.secondaryContainer,
            Color(0xFFFFD700), // Gold
            Color(0xFFFF6B6B), // Coral
            Color(0xFF4ECDC4), // Teal
            Color(0xFFFFE66D) // Yellow
        )

    val progress = remember { Animatable(0f) }

    val particles =
        remember {
            List(particleCount) {
                val angle = Random.nextFloat() * 2 * Math.PI.toFloat()
                val speed = Random.nextFloat() * 8f + 4f
                ConfettiParticle(
                    x = 0.5f, // Start from center-top
                    y = 0f,
                    velocityX = cos(angle) * speed * 0.5f,
                    velocityY = sin(angle) * speed * 0.3f + 2f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 10f - 5f,
                    color = colors.random(),
                    size = Random.nextFloat() * 12f + 6f
                )
            }
        }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
        onComplete()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val gravity = 0.15f
        val currentProgress = progress.value

        particles.forEach { particle ->
            val time = currentProgress * durationMillis / 16f // Time in frames

            // Calculate position with physics
            val x = size.width * particle.x + particle.velocityX * time
            val y =
                particle.y * size.height + particle.velocityY * time + 0.5f * gravity * time * time

            // Fade out towards the end
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)

            // Only draw if within bounds
            if (y < size.height && y > -particle.size && x > -particle.size && x < size.width + particle.size) {
                val rotation = particle.rotation + particle.rotationSpeed * time

                rotate(rotation, pivot = Offset(x, y)) {
                    // Draw rectangle confetti
                    drawRect(
                        color = particle.color.copy(alpha = alpha),
                        topLeft = Offset(x - particle.size / 2, y - particle.size / 4),
                        size = Size(particle.size, particle.size / 2)
                    )
                }
            }
        }
    }
}

@Composable
fun CelebrationStars(
    modifier: Modifier = Modifier,
    starCount: Int = 12,
    durationMillis: Int = 2000
) {
    val progress = remember { Animatable(0f) }
    val starColor = MaterialTheme.colorScheme.primary

    val stars =
        remember {
            List(starCount) {
                val angle = (it.toFloat() / starCount) * 2 * Math.PI.toFloat()
                Triple(
                    cos(angle),
                    sin(angle),
                    Random.nextFloat() * 0.3f + 0.7f // Size variation
                )
            }
        }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
        )
    }

    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius =
            kotlin.comparisons.minOf(size.width, size.height) / 2 * 0.8f
        val currentProgress = progress.value

        stars.forEach { (dirX, dirY, sizeMultiplier) ->
            // Burst outward animation
            val burstProgress = (currentProgress * 2).coerceAtMost(1f)
            val fadeProgress = ((currentProgress - 0.5f) * 2).coerceIn(0f, 1f)

            val radius = maxRadius * burstProgress
            val x = centerX + dirX * radius
            val y = centerY + dirY * radius
            val starSize = 8f * sizeMultiplier * (1f - fadeProgress * 0.5f)
            val alpha = 1f - fadeProgress

            // Draw star shape (simplified as a small diamond)
            drawCircle(
                color = starColor.copy(alpha = alpha),
                radius = starSize,
                center = Offset(x, y)
            )
        }
    }
}
