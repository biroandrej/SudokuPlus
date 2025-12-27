package sk.awisoft.sudokuplus.ui.settings.playgames

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CloudSync
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PreferenceRow
import sk.awisoft.sudokuplus.ui.components.PreferenceRowSwitch
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.settings.SettingsCategory
import sk.awisoft.sudokuplus.ui.settings.SettingsScaffoldLazyColumn

@Destination<RootGraph>(style = AnimatedNavigation::class)
@Composable
fun PlayGamesScreen(
    viewModel: PlayGamesViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val activity = context as Activity
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val playGamesEnabled by viewModel.playGamesEnabled.collectAsStateWithLifecycle(initialValue = false)
    val isSignedIn by viewModel.isSignedIn.collectAsStateWithLifecycle()
    val playerInfo by viewModel.playerInfo.collectAsStateWithLifecycle()

    LaunchedEffect(playGamesEnabled) {
        if (playGamesEnabled && !isSignedIn) {
            viewModel.silentSignIn(context)
        }
    }

    SettingsScaffoldLazyColumn(
        titleText = stringResource(R.string.play_games_title),
        navigator = navigator,
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        ScrollbarLazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
        ) {
            item {
                PreferenceRowSwitch(
                    title = stringResource(R.string.play_games_enable),
                    subtitle = stringResource(R.string.play_games_enable_summary),
                    checked = playGamesEnabled,
                    onClick = {
                        viewModel.setPlayGamesEnabled(!playGamesEnabled)
                    },
                    painter = rememberVectorPainter(Icons.Outlined.SportsEsports)
                )
            }

            if (playGamesEnabled) {
                item {
                    SettingsCategory(title = stringResource(R.string.play_games_sign_in))
                }

                item {
                    if (isSignedIn && playerInfo != null) {
                        PreferenceRow(
                            title = stringResource(R.string.play_games_sign_out),
                            subtitle = stringResource(
                                R.string.play_games_signed_in_as,
                                playerInfo?.displayName ?: ""
                            ),
                            onClick = { viewModel.signOut() },
                            painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Logout)
                        )
                    } else {
                        PreferenceRow(
                            title = stringResource(R.string.play_games_sign_in),
                            subtitle = stringResource(R.string.play_games_not_signed_in),
                            onClick = {
                                viewModel.signIn(activity) { success ->
                                    if (!success) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                context.getString(R.string.play_games_sign_in_failed)
                                            )
                                        }
                                    }
                                }
                            },
                            painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Login)
                        )
                    }
                }

                if (isSignedIn) {
                    item {
                        SettingsCategory(title = stringResource(R.string.play_games_achievements))
                    }

                    item {
                        PreferenceRow(
                            title = stringResource(R.string.play_games_achievements),
                            subtitle = stringResource(R.string.play_games_achievements_summary),
                            onClick = { viewModel.showAchievements(activity) },
                            painter = rememberVectorPainter(Icons.Outlined.EmojiEvents)
                        )
                    }

                    item {
                        PreferenceRow(
                            title = stringResource(R.string.play_games_leaderboards),
                            subtitle = stringResource(R.string.play_games_leaderboards_summary),
                            onClick = { viewModel.showLeaderboards(activity) },
                            painter = rememberVectorPainter(Icons.Outlined.Leaderboard)
                        )
                    }

                    item {
                        PreferenceRow(
                            title = stringResource(R.string.play_games_sync),
                            subtitle = stringResource(R.string.play_games_sync_summary),
                            onClick = { viewModel.syncProgress() },
                            painter = rememberVectorPainter(Icons.Outlined.CloudSync)
                        )
                    }
                }
            }
        }
    }
}
