package sk.awisoft.sudokuplus.ui.components.navigation_bar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import sk.awisoft.sudokuplus.NavGraphs
import sk.awisoft.sudokuplus.appCurrentDestinationAsState
import sk.awisoft.sudokuplus.destinations.MoreScreenDestination
import sk.awisoft.sudokuplus.startAppDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

@Composable
fun NavigationBarComponent(
    navController: NavController,
    isVisible: Boolean,
    updateAvailable: Boolean = false,
) {
    val directions = listOf(
        NavigationBarDestination.Statistics,
        NavigationBarDestination.Home,
        NavigationBarDestination.More
    )

    val currentDestination = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    if (isVisible) {
        NavigationBar {
            directions.forEach { destination ->
                val isSelected = currentDestination == destination.direction

                // Animated icon scale with spring bounce
                val iconScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "nav icon scale"
                )

                NavigationBarItem(
                    icon = {
                        if (destination.direction.route == MoreScreenDestination.route
                            && updateAvailable
                        ) {
                            BadgedBox(
                                badge = {
                                    Badge()
                                }
                            ) {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = null,
                                    modifier = Modifier.scale(iconScale)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = null,
                                modifier = Modifier.scale(iconScale)
                            )
                        }
                    },
                    selected = isSelected,
                    label = {
                        Text(
                            text = stringResource(destination.label),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    onClick = {
                        navController.toDestinationsNavigator().navigate(destination.direction) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}