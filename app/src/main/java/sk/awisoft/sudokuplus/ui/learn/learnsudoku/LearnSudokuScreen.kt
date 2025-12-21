package sk.awisoft.sudokuplus.ui.learn.learnsudoku

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.LearnBasicDestination
import sk.awisoft.sudokuplus.destinations.LearnHiddenPairsDestination
import sk.awisoft.sudokuplus.destinations.LearnNakedPairsDestination
import sk.awisoft.sudokuplus.destinations.LearnSudokuRulesDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.learn.components.LearnRowItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun LearnSudokuScreen(
    navigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn {
            item {
                LearnRowItem(
                    title = stringResource(R.string.learn_sudoku_rules),
                    onClick = { navigator.navigate(LearnSudokuRulesDestination()) }
                )
                LearnRowItem(
                    title = stringResource(R.string.learn_basic_title),
                    onClick = { navigator.navigate(LearnBasicDestination()) }
                )
                LearnRowItem(
                    title = stringResource(R.string.naked_pairs_title),
                    onClick = { navigator.navigate(LearnNakedPairsDestination()) }
                )
                LearnRowItem(
                    title = stringResource(R.string.learn_hidden_pairs_title),
                    onClick = { navigator.navigate(LearnHiddenPairsDestination()) }
                )
            }
        }
    }
}
