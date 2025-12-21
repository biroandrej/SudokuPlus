package sk.awisoft.sudokuplus.ui.components.navigation_bar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import sk.awisoft.sudokuplus.NavGraphs
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

@Composable
fun NavigationBarComponent(
    navController: NavController,
    isVisible: Boolean,
) {
    val directions = listOf(
        NavigationBarDestination.Home,
        NavigationBarDestination.Statistics,
        NavigationBarDestination.More
    )

    val currentDestination = navController.currentDestinationAsState().value
        ?: HomeScreenDestination

    if (isVisible) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = 3.dp
        ) {
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
                    alwaysShowLabel = false,
                    icon = {
                        Icon(
                            imageVector = destination.icon,
                            contentDescription = null,
                            modifier = Modifier.scale(iconScale)
                        )
                    },
                    selected = isSelected,
                    label = {
                        Text(
                            text = stringResource(destination.label),
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
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
