package sk.awisoft.sudokuplus.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import com.ramcosta.composedestinations.spec.DestinationStyle

object AnimatedNavigation : DestinationStyle.Animated() {
    private const val TRANSITION_DURATION = 300
    private const val FADE_DURATION = 150

    override val enterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
            // Forward navigation - slide in from right
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec =
                tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) +
                fadeIn(
                    animationSpec =
                    tween(
                        durationMillis = FADE_DURATION,
                        delayMillis = 50
                    )
                ) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
        }

    override val exitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
            // Current screen slides out to left when new screen enters
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec =
                tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) +
                fadeOut(
                    animationSpec = tween(durationMillis = FADE_DURATION)
                ) +
                scaleOut(
                    targetScale = 0.95f,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
        }

    override val popEnterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) = {
            // Back navigation - slide in from left
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec =
                tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) +
                fadeIn(
                    animationSpec =
                    tween(
                        durationMillis = FADE_DURATION,
                        delayMillis = 50
                    )
                ) +
                scaleIn(
                    initialScale = 0.95f,
                    animationSpec = tween(durationMillis = TRANSITION_DURATION)
                )
        }

    override val popExitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) = {
            // Current screen slides out to right when going back
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 4 },
                animationSpec =
                tween(
                    durationMillis = TRANSITION_DURATION,
                    easing = FastOutSlowInEasing
                )
            ) +
                fadeOut(
                    animationSpec = tween(durationMillis = FADE_DURATION)
                ) +
                scaleOut(
                    targetScale = 0.92f,
                    animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        }
}
