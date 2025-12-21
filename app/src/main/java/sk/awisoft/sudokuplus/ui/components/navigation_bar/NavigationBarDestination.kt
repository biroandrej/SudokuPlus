package sk.awisoft.sudokuplus.ui.components.navigation_bar

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.StackedBarChart
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.HomeScreenDestination
import sk.awisoft.sudokuplus.destinations.MoreScreenDestination
import sk.awisoft.sudokuplus.destinations.StatisticsScreenDestination

sealed class NavigationBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @param: StringRes val label: Int
) {
    data object Home : NavigationBarDestination(
        HomeScreenDestination,
        Icons.Rounded.Home,
        R.string.nav_bar_home
    )

    data object Statistics : NavigationBarDestination(
        StatisticsScreenDestination,
        Icons.Rounded.StackedBarChart,
        R.string.nav_bar_statistics
    )

    data object More : NavigationBarDestination(
        MoreScreenDestination,
        Icons.Rounded.MoreVert,
        R.string.nav_bar_more
    )
}