package sk.awisoft.sudokuplus.ui.components.navigation_bar

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.MoreScreenDestination
import sk.awisoft.sudokuplus.destinations.StatisticsScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

sealed class NavigationBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    data object Home : NavigationBarDestination(
        HomeScreenDestination,
        Icons.Rounded.Home,
        R.string.nav_bar_home
    )

    data object Statistics : NavigationBarDestination(
        StatisticsScreenDestination,
        Icons.Rounded.Info,
        R.string.nav_bar_statistics
    )

    data object More : NavigationBarDestination(
        MoreScreenDestination,
        Icons.Rounded.MoreHoriz,
        R.string.nav_bar_more
    )
}