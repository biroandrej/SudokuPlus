package sk.awisoft.sudokuplus.ui.learn.learnapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.ToolbarTutorialScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.learn.components.LearnRowItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(style = AnimatedNavigation::class)
@Composable
fun LearnAppScreen(
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn {
            item {
                LearnRowItem(
                    title = stringResource(R.string.learn_app_toolbar),
                    subtitle = stringResource(R.string.learn_app_toolbar_desc),
                    onClick = { navigator.navigate(ToolbarTutorialScreenDestination()) }
                )
            }
        }
    }
}