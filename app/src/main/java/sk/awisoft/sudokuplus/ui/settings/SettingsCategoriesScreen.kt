package sk.awisoft.sudokuplus.ui.settings

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.destinations.PlayGamesScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsAdvancedHintScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsAppearanceScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsAssistanceScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsGameplayScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsLanguageScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsNotificationsScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsOtherScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.components.collapsing_topappbar.CollapsingTitle
import sk.awisoft.sudokuplus.ui.components.collapsing_topappbar.CollapsingTopAppBar
import sk.awisoft.sudokuplus.ui.components.collapsing_topappbar.rememberTopAppBarScrollBehavior
import sk.awisoft.sudokuplus.ui.util.getCurrentLocaleString

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun SettingsCategoriesScreen(navigator: DestinationsNavigator, launchedFromGame: Boolean = false) {
    val context = LocalContext.current
    val currentLanguage by remember { mutableStateOf(getCurrentLocaleString(context)) }
    SettingsScaffoldLazyColumn(
        titleText = stringResource(R.string.settings_title),
        navigator = navigator
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_appearance),
                    subtitle = stringResource(R.string.perf_appearance_summary),
                    onClick = {
                        navigator.navigate(SettingsAppearanceScreenDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Palette)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_gameplay),
                    subtitle = stringResource(R.string.perf_gameplay_summary),
                    onClick = {
                        navigator.navigate(SettingsGameplayScreenDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Extension)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_assistance),
                    subtitle = stringResource(R.string.perf_assistance_summary),
                    onClick = {
                        navigator.navigate(SettingsAssistanceScreenDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.TipsAndUpdates)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.advanced_hint_title),
                    subtitle = stringResource(R.string.advanced_hint_summary),
                    onClick = { navigator.navigate(SettingsAdvancedHintScreenDestination()) },
                    painter = rememberVectorPainter(Icons.Rounded.AutoAwesome)
                )
            }

            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_app_language),
                    subtitle = currentLanguage,
                    onClick = {
                        navigator.navigate(SettingsLanguageScreenDestination())
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Language)
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.notifications_title),
                    subtitle = stringResource(R.string.notifications_daily_challenge_desc),
                    onClick = {
                        navigator.navigate(SettingsNotificationsScreenDestination)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.Notifications)
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.play_games_title),
                    subtitle = stringResource(R.string.play_games_summary),
                    onClick = {
                        navigator.navigate(PlayGamesScreenDestination)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.SportsEsports)
                )
            }
            item {
                PreferenceRow(
                    title = stringResource(R.string.pref_other),
                    subtitle = stringResource(R.string.perf_other_summary),
                    onClick = {
                        navigator.navigate(
                            SettingsOtherScreenDestination(launchedFromGame = launchedFromGame)
                        )
                    },
                    painter = rememberVectorPainter(Icons.Outlined.MoreHoriz)
                )
            }
        }
    }
}

@Composable
fun SettingsCategory(modifier: Modifier = Modifier, title: String) {
    Row(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(start = 16.dp, bottom = 16.dp, top = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SettingsScaffoldLazyColumn(
    navigator: DestinationsNavigator,
    titleText: String,
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = rememberTopAppBarScrollBehavior()

    Scaffold(
        modifier =
        Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            snackbarHostState?.let {
                SnackbarHost(it)
            }
        },
        topBar = {
            CollapsingTopAppBar(
                collapsingTitle = CollapsingTitle.Companion.medium(titleText = titleText),
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_round_arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
