package sk.awisoft.sudokuplus.ui.settings.assistance

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.LooksOne
import androidx.compose.material.icons.outlined.Pin
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.PreferenceRowSwitch
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.settings.SelectionDialog
import sk.awisoft.sudokuplus.ui.settings.SettingsScaffoldLazyColumn
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun SettingsAssistanceScreen(
    viewModel: SettingsAssistanceViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    var mistakesDialog by rememberSaveable { mutableStateOf(false) }

    val highlightMistakes by viewModel.highlightMistakes.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_HIGHLIGHT_MISTAKES
    )
    val autoEraseNotes by viewModel.autoEraseNotes.collectAsStateWithLifecycle(initialValue = PreferencesConstants.Companion.DEFAULT_AUTO_ERASE_NOTES)
    val highlightIdentical by viewModel.highlightIdentical.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_HIGHLIGHT_IDENTICAL
    )
    val remainingUse by viewModel.remainingUse.collectAsStateWithLifecycle(initialValue = PreferencesConstants.Companion.DEFAULT_REMAINING_USES)

    SettingsScaffoldLazyColumn(
        titleText = stringResource(R.string.pref_assistance),
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_mistakes_check),
                    subtitle = when (highlightMistakes) {
                        0 -> stringResource(R.string.pref_mistakes_check_off)
                        1 -> stringResource(R.string.pref_mistakes_check_violations)
                        2 -> stringResource(R.string.pref_mistakes_check_final)
                        else -> stringResource(R.string.pref_mistakes_check_off)
                    },
                    onClick = { mistakesDialog = true },
                    painter = rememberVectorPainter(Icons.Outlined.Adjust)
                )
            }

            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.pref_highlight_identical),
                    subtitle = stringResource(R.string.pref_highlight_identical_summ),
                    checked = highlightIdentical,
                    onClick = {
                        viewModel.updateHighlightIdentical(!highlightIdentical)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.LooksOne)
                )
            }

            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.pref_remaining_uses),
                    subtitle = stringResource(R.string.pref_remaining_uses_summ),
                    checked = remainingUse,
                    onClick = { viewModel.updateRemainingUse(!remainingUse) },
                    painter = rememberVectorPainter(Icons.Outlined.Pin)
                )

            }

            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.pref_auto_erase_notes),
                    checked = autoEraseNotes,
                    onClick = { viewModel.updateAutoEraseNotes(!autoEraseNotes) },
                    painter = rememberVectorPainter(Icons.Outlined.AutoFixHigh)
                )
            }
        }

        if (mistakesDialog) {
            SelectionDialog(
                title = stringResource(R.string.pref_mistakes_check),
                selections = listOf(
                    stringResource(R.string.pref_mistakes_check_off),
                    stringResource(R.string.pref_mistakes_check_violations),
                    stringResource(R.string.pref_mistakes_check_final)
                ),
                selected = highlightMistakes,
                onSelect = { index ->
                    viewModel.updateMistakesHighlight(index)
                },
                onDismiss = { mistakesDialog = false }
            )
        }
    }
}