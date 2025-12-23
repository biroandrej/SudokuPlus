package sk.awisoft.sudokuplus.ui.learn.learnapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.learn.components.TutorialBase
import sk.awisoft.sudokuplus.ui.onboarding.FirstGameScreen

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun ToolbarTutorialScreen(navigator: DestinationsNavigator) {
    TutorialBase(
        title = stringResource(R.string.learn_app_toolbar),
        navigator = navigator
    ) {
        FirstGameScreen()
        Text(
            text = stringResource(R.string.learn_app_toolbar_notes_menu),
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}
