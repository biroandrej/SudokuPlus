package sk.awisoft.sudokuplus.ui.home

import android.Manifest
import android.os.Build
import android.text.format.DateUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.utils.toFormattedString
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.destinations.DailyChallengeCalendarScreenDestination
import sk.awisoft.sudokuplus.destinations.GameScreenDestination
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.ScrollbarLazyColumn
import sk.awisoft.sudokuplus.ui.components.board.BoardPreview
import sk.awisoft.sudokuplus.ui.home.components.DailyChallengeCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime
import kotlin.math.sqrt
import kotlin.time.toKotlinDuration

@OptIn(ExperimentalMaterial3Api::class)
@Destination(style = AnimatedNavigation::class)
@RootNavGraph(start = true)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    var continueGameDialog by rememberSaveable { mutableStateOf(false) }
    var lastGamesBottomSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val lastGame by viewModel.lastSavedGame.collectAsStateWithLifecycle()
    val lastGames by viewModel.lastGames.collectAsStateWithLifecycle(initialValue = emptyMap())
    val saveSelectedGameDifficultyType by viewModel.saveSelectedGameDifficultyType.collectAsStateWithLifecycle(
        false
    )
    val lastSelectedGameDifficultyType by viewModel.lastSelectedGameDifficultyType.collectAsStateWithLifecycle(
        Pair(
            GameDifficulty.Easy, GameType.Default9x9
        )
    )

    // Daily Challenge
    val dailyChallenge by viewModel.dailyChallenge.collectAsStateWithLifecycle()
    val isDailyLoading by viewModel.isDailyLoading.collectAsStateWithLifecycle()
    val dailyCurrentStreak by viewModel.dailyCurrentStreak.collectAsStateWithLifecycle()

    Notifications(viewModel = viewModel)

    LaunchedEffect(saveSelectedGameDifficultyType) {
        if (saveSelectedGameDifficultyType) {
            val (difficulty, type) = lastSelectedGameDifficultyType
            viewModel.selectedDifficulty = difficulty
            viewModel.selectedType = type
        }
    }

    val hasGameInProgress = (lastGame != null && !lastGame!!.completed)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            if (hasGameInProgress) {
                ExtendedFloatingActionButton(
                    onClick = { continueGameDialog = true },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null
                        )
                    },
                    text = { Text(stringResource(R.string.new_game)) }
                )
            }
        }
    ) { paddingValues ->
        if (viewModel.readyToPlay) {
            viewModel.readyToPlay = false

            runBlocking {
                navigator.navigate(
                    GameScreenDestination(
                        gameUid = viewModel.insertedBoardUid,
                        playedBefore = false
                    )
                )
            }
        }

        // Daily Challenge navigation
        if (viewModel.dailyChallengeReadyToPlay) {
            viewModel.dailyChallengeReadyToPlay = false

            runBlocking {
                navigator.navigate(
                    GameScreenDestination(
                        gameUid = viewModel.dailyChallengeGameUid,
                        playedBefore = false,
                        isDailyChallenge = true
                    )
                )
            }
        }

        ScrollbarLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                DailyChallengeCard(
                    challenge = dailyChallenge,
                    currentStreak = dailyCurrentStreak,
                    isLoading = isDailyLoading,
                    onPlay = { viewModel.playDailyChallenge() },
                    onViewCalendar = { navigator.navigate(DailyChallengeCalendarScreenDestination) }
                )
            }

            item {
                HomeHeroCard(
                    difficultyLabel = stringResource(viewModel.selectedDifficulty.resName),
                    typeLabel = stringResource(viewModel.selectedType.resName),
                    onDecreaseDifficulty = { viewModel.changeDifficulty(-1) },
                    onIncreaseDifficulty = { viewModel.changeDifficulty(1) },
                    onDecreaseType = { viewModel.changeType(-1) },
                    onIncreaseType = { viewModel.changeType(1) },
                    canContinue = hasGameInProgress,
                    onContinue = {
                        if (lastGames.size <= 1) {
                            lastGame?.let {
                                navigator.navigate(
                                    GameScreenDestination(
                                        gameUid = it.uid,
                                        playedBefore = true
                                    )
                                )
                            }
                        } else {
                            lastGamesBottomSheet = true
                        }
                    },
                    onPlay = {
                        viewModel.giveUpLastGame()
                        viewModel.startGame()
                    }
                )
            }

            if (lastGames.isNotEmpty()) {
                item {
                    HomeSectionHeader(
                        title = pluralStringResource(
                            id = R.plurals.last_x_games,
                            count = lastGames.size,
                            lastGames.size
                        ),
                        actionText = stringResource(R.string.more_title),
                        onActionClick = { lastGamesBottomSheet = true }
                    )
                }
                items(lastGames.toList().take(3)) { item ->
                    SavedSudokuPreview(
                        board = item.first.currentBoard,
                        difficulty = stringResource(item.second.difficulty.resName),
                        type = stringResource(item.second.type.resName),
                        savedGame = item.first,
                        onClick = {
                            navigator.navigate(
                                GameScreenDestination(
                                    gameUid = item.first.uid,
                                    playedBefore = true
                                )
                            )
                        }
                    )
                }
            }
        }


        if (viewModel.isGenerating || viewModel.isSolving) {
            GeneratingDialog(
                onDismiss = { },
                text = when {
                    viewModel.isGenerating -> stringResource(R.string.dialog_generating)
                    viewModel.isSolving -> stringResource(R.string.dialog_solving)
                    else -> ""
                }
            )
        }

        if (continueGameDialog) {
            AlertDialog(
                title = { Text(stringResource(R.string.dialog_new_game)) },
                text = { Text(stringResource(R.string.dialog_new_game_text)) },
                confirmButton = {
                    TextButton(onClick = {
                        continueGameDialog = false
                        viewModel.giveUpLastGame()
                        viewModel.startGame()
                    }) {
                        Text(stringResource(R.string.dialog_new_game_positive))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { continueGameDialog = false }) {
                        Text(stringResource(R.string.action_cancel))
                    }
                },
                onDismissRequest = {
                    continueGameDialog = false
                }
            )
        }

        if (lastGamesBottomSheet) {
            ModalBottomSheet(onDismissRequest = { lastGamesBottomSheet = false }) {
                Text(
                    text = pluralStringResource(
                        id = R.plurals.last_x_games,
                        count = lastGames.size,
                        lastGames.size
                    ),
                    modifier = Modifier.padding(start = 12.dp),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(12.dp))
                ScrollbarLazyColumn(
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clip(MaterialTheme.shapes.large),
                ) {
                    items(lastGames.toList()) { item ->
                        SavedSudokuPreview(
                            board = item.first.currentBoard,
                            difficulty = stringResource(item.second.difficulty.resName),
                            type = stringResource(item.second.type.resName),
                            savedGame = item.first,
                            onClick = {
                                navigator.navigate(
                                    GameScreenDestination(
                                        gameUid = item.first.uid,
                                        playedBefore = true
                                    )
                                )
                                lastGamesBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Notifications(viewModel: HomeViewModel) {
    // Notification permission
    val shouldShowNotificationPermission by viewModel.shouldShowNotificationPermission.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
    }

    // Show notification permission dialog on Android 13+
    if (shouldShowNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AlertDialog(
            onDismissRequest = { viewModel.onNotificationPermissionRequested() },
            title = { Text(stringResource(R.string.notification_permission_dialog_title)) },
            text = { Text(stringResource(R.string.notification_permission_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onNotificationPermissionRequested()
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                ) {
                    Text(stringResource(R.string.notification_permission_dialog_allow))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onNotificationPermissionRequested() }) {
                    Text(stringResource(R.string.notification_permission_dialog_later))
                }
            }
        )
    }
}

@Composable
fun GeneratingDialog(
    onDismiss: () -> Unit,
    text: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Column(
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeHeroCard(
    difficultyLabel: String,
    typeLabel: String,
    onDecreaseDifficulty: () -> Unit,
    onIncreaseDifficulty: () -> Unit,
    onDecreaseType: () -> Unit,
    onIncreaseType: () -> Unit,
    canContinue: Boolean,
    onContinue: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$difficultyLabel • $typeLabel",
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            HorizontalDivider()

            HomePickerRow(
                value = stringResource(R.string.saved_game_difficulty, difficultyLabel),
                onPrevious = onDecreaseDifficulty,
                onNext = onIncreaseDifficulty
            )
            HomePickerRow(
                value = stringResource(R.string.saved_game_type, typeLabel),
                onPrevious = onDecreaseType,
                onNext = onIncreaseType
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (canContinue) {
                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.action_continue))
                    }
                } else {
                    Button(
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (canContinue) stringResource(R.string.action_continue)
                            else stringResource(R.string.action_play)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePickerRow(
    value: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_keyboard_arrow_left_24),
                    contentDescription = null
                )
            }
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
                },
                label = "Picker value"
            ) { animatedValue ->
                Text(
                    text = animatedValue,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(onClick = onNext) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_keyboard_arrow_right_24),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun HomeSectionHeader(
    title: String,
    actionText: String?,
    onActionClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(actionText)
            }
        }
    }
}

@Composable
fun SavedSudokuPreview(
    board: String,
    difficulty: String,
    type: String,
    savedGame: SavedGame,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    val lastPlayedRelative: String? = if (savedGame.lastPlayed != null) {
        remember(savedGame) {
            DateUtils.getRelativeTimeSpanString(
                savedGame.lastPlayed.toEpochSecond() * 1000L,
                ZonedDateTime.now().toEpochSecond() * 1000L,
                DateUtils.MINUTE_IN_MILLIS
            ).toString()
        }
    } else {
        null
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .size(72.dp)
            ) {
                BoardPreview(
                    size = sqrt(board.length.toFloat()).toInt(),
                    boardString = board
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$difficulty $type",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(
                        R.string.history_item_time,
                        savedGame.timer.toKotlinDuration().toFormattedString()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (lastPlayedRelative != null) {
                    Text(
                        text = lastPlayedRelative,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
