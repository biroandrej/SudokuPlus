package sk.awisoft.sudokuplus.ui.game

import android.content.ClipData
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlin.collections.filter
import kotlin.collections.plus
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.ads.AdsManager
import sk.awisoft.sudokuplus.ai.AIHintResponse
import sk.awisoft.sudokuplus.core.Cell
import sk.awisoft.sudokuplus.core.Note
import sk.awisoft.sudokuplus.core.PreferencesConstants
import sk.awisoft.sudokuplus.core.qqwing.Cage
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.qqwing.advanced_hint.AdvancedHintData
import sk.awisoft.sudokuplus.core.utils.SudokuParser
import sk.awisoft.sudokuplus.core.xp.XPResult
import sk.awisoft.sudokuplus.data.database.model.AchievementDefinition
import sk.awisoft.sudokuplus.destinations.SettingsAdvancedHintScreenDestination
import sk.awisoft.sudokuplus.destinations.SettingsCategoriesScreenDestination
import sk.awisoft.sudokuplus.review.ReviewManager
import sk.awisoft.sudokuplus.ui.achievements.AchievementUnlockDialog
import sk.awisoft.sudokuplus.ui.components.AIHintErrorDialog
import sk.awisoft.sudokuplus.ui.components.AIHintLoadingDialog
import sk.awisoft.sudokuplus.ui.components.AIHintResultDialog
import sk.awisoft.sudokuplus.ui.components.AdvancedHintContainer
import sk.awisoft.sudokuplus.ui.components.AnimatedNavigation
import sk.awisoft.sudokuplus.ui.components.PremiumUpsellDialog
import sk.awisoft.sudokuplus.ui.components.board.Board
import sk.awisoft.sudokuplus.ui.game.components.DefaultGameKeyboard
import sk.awisoft.sudokuplus.ui.game.components.GameMenu
import sk.awisoft.sudokuplus.ui.game.components.NotesMenu
import sk.awisoft.sudokuplus.ui.game.components.ToolBarItem
import sk.awisoft.sudokuplus.ui.game.components.ToolbarItem
import sk.awisoft.sudokuplus.ui.game.components.ToolbarItemHeight
import sk.awisoft.sudokuplus.ui.game.components.UndoRedoMenu
import sk.awisoft.sudokuplus.ui.onboarding.FirstGameDialog
import sk.awisoft.sudokuplus.ui.theme.SudokuPlusTheme
import sk.awisoft.sudokuplus.ui.util.ReverseArrangement
import sk.awisoft.sudokuplus.ui.util.findActivity
import sk.awisoft.sudokuplus.ui.xp.LevelUpDialog
import sk.awisoft.sudokuplus.ui.xp.XPEarnedDisplay

@Destination<RootGraph>(
    style = AnimatedNavigation::class,
    navArgs = GameScreenNavArgs::class
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel = hiltViewModel(), navigator: DestinationsNavigator) {
    val localView = LocalView.current // vibration
    val clipboardManager = LocalClipboard.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    val darkThemeSetting by viewModel.darkTheme.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_DARK_THEME
    )
    val resolvedDarkTheme =
        when (darkThemeSetting) {
            1 -> false
            2 -> true
            else -> isSystemInDarkTheme()
        }
    val darkTheme = resolvedDarkTheme

    val firstGame by viewModel.firstGame.collectAsStateWithLifecycle(initialValue = false)
    val resetTimer by viewModel.resetTimerOnRestart.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_GAME_RESET_TIMER
    )
    val mistakesLimit by viewModel.mistakesLimit.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_MISTAKES_LIMIT
    )
    val errorHighlight by viewModel.mistakesMethod.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_HIGHLIGHT_MISTAKES
    )
    val keepScreenOn by viewModel.keepScreenOn.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_KEEP_SCREEN_ON
    )
    val remainingUse by viewModel.remainingUse.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_REMAINING_USES
    )
    val highlightIdentical by viewModel.identicalHighlight.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_HIGHLIGHT_IDENTICAL
    )
    val positionLines by viewModel.positionLines.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_POSITION_LINES
    )
    val crossHighlight by viewModel.crossHighlight.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_BOARD_CROSS_HIGHLIGHT
    )
    val funKeyboardOverNum by viewModel.funKeyboardOverNum.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_FUN_KEYBOARD_OVER_NUM
    )

    val fontSizeFactor by viewModel.fontSize.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.Companion.DEFAULT_FONT_SIZE_FACTOR
    )
    val fontSizeValue by remember(fontSizeFactor, viewModel.gameType) {
        mutableStateOf(
            viewModel.getFontSize(factor = fontSizeFactor)
        )
    }
    val advancedHintEnabled by viewModel.advancedHintEnabled.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_ADVANCED_HINT
    )
    val advancedHintMode by viewModel.advancedHintMode.collectAsStateWithLifecycle(false)
    val advancedHintData by viewModel.advancedHintData.collectAsStateWithLifecycle(null)
    if (keepScreenOn) {
        KeepScreenOn()
    }

    var showRewardedHintDialog by rememberSaveable { mutableStateOf(false) }
    var unlockedAchievements by remember {
        mutableStateOf<List<AchievementDefinition>>(
            emptyList()
        )
    }
    var xpResult by remember { mutableStateOf<XPResult?>(null) }
    var showLevelUpDialog by remember { mutableStateOf<Int?>(null) }

    // AI Hint state
    var showAIHintUpsellDialog by rememberSaveable { mutableStateOf(false) }
    var aiHintResult by remember { mutableStateOf<AIHintResponse.Success?>(null) }
    var aiHintError by remember { mutableStateOf<String?>(null) }
    val aiHintState by viewModel.aiHintState.collectAsStateWithLifecycle()
    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val freeAIHintsRemaining by viewModel.freeAIHintsRemaining.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        AdsManager.preloadInterstitial(context)
        AdsManager.preloadRewarded(context)
    }

    if (firstGame) {
        viewModel.pauseTimer()
        FirstGameDialog(
            onFinished = {
                viewModel.setFirstGameFalse()
                viewModel.startTimer()
            }
        )
    }

    var restartButtonAngleState by remember { mutableFloatStateOf(0f) }
    val restartButtonAnimation: Float by animateFloatAsState(
        targetValue = restartButtonAngleState,
        animationSpec = tween(durationMillis = 250),
        label = "restartButtonAnimation"
    )

    val timerEnabled by viewModel.timerEnabled.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_SHOW_TIMER
    )
    val hintsDisabled by viewModel.disableHints.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_HINTS_DISABLED
    )
    val hintsRemaining by viewModel.hintsRemaining.collectAsStateWithLifecycle(
        initialValue = PreferencesConstants.DEFAULT_HINTS_PER_GAME
    )

    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                GameViewModel.UiEvent.NoHintsRemaining -> {
                    snackbarHostState.showSnackbar(context.getString(R.string.hints_no_remaining))
                }
                GameViewModel.UiEvent.ShowInterstitial -> {
                    context.findActivity()?.let { activity ->
                        AdsManager.showInterstitialIfAvailable(activity)
                    }
                }
                GameViewModel.UiEvent.RequestRewardedHint -> {
                    showRewardedHintDialog = true
                }
                is GameViewModel.UiEvent.AchievementsUnlocked -> {
                    unlockedAchievements = event.achievements
                }
                is GameViewModel.UiEvent.XPEarned -> {
                    xpResult = event.xpResult
                }
                is GameViewModel.UiEvent.LevelUp -> {
                    showLevelUpDialog = event.newLevel
                }
                is GameViewModel.UiEvent.RequestReview -> {
                    context.findActivity()?.let { activity ->
                        ReviewManager.requestReviewIfEligible(activity, event.completedGames)
                    }
                }
                GameViewModel.UiEvent.RequestAIHintUpsell -> {
                    showAIHintUpsellDialog = true
                }
                is GameViewModel.UiEvent.AIHintResult -> {
                    aiHintResult = event.response
                }
                is GameViewModel.UiEvent.AIHintError -> {
                    aiHintError = event.message
                }
            }
        }
    }

    if (showRewardedHintDialog) {
        RewardedHintDialog(
            onConfirm = {
                showRewardedHintDialog = false
                val activity = context.findActivity()
                if (activity == null) {
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.hints_no_remaining)
                        )
                    }
                    return@RewardedHintDialog
                }
                val shown = AdsManager.showRewardedIfAvailable(activity) {
                    viewModel.applyRewardedHint()
                }
                if (!shown) {
                    snackbarScope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(R.string.hints_no_remaining)
                        )
                    }
                }
            },
            onDismiss = { showRewardedHintDialog = false }
        )
    }

    // AI Hint Loading Dialog
    if (aiHintState is AIHintResponse.Loading) {
        AIHintLoadingDialog(
            onDismiss = { viewModel.dismissAIHint() }
        )
    }

    // AI Hint Result Dialog
    aiHintResult?.let { result ->
        AIHintResultDialog(
            response = result,
            onApply = {
                viewModel.applyAIHint(result)
                aiHintResult = null
            },
            onDismiss = { aiHintResult = null }
        )
    }

    // AI Hint Error Dialog
    aiHintError?.let { error ->
        AIHintErrorDialog(
            message = error,
            onDismiss = { aiHintError = null },
            onRetry = {
                aiHintError = null
                viewModel.getSmartHint()
            }
        )
    }

    // Premium Upsell Dialog
    if (showAIHintUpsellDialog) {
        PremiumUpsellDialog(
            onWatchAd = {
                showAIHintUpsellDialog = false
                val activity = context.findActivity()
                if (activity != null) {
                    AdsManager.showRewardedIfAvailable(activity) {
                        viewModel.grantHintFromAd()
                    }
                }
            },
            onUpgrade = {
                showAIHintUpsellDialog = false
                navigator.navigate(
                    SettingsCategoriesScreenDestination(
                        launchedFromGame = true
                    )
                )
            },
            onDismiss = { showAIHintUpsellDialog = false },
            freeHintsRemaining = freeAIHintsRemaining
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                AdsManager.BannerAd(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        },
        topBar = {
            GameTopAppBar(
                endGame = viewModel.endGame,
                showSolution = viewModel.showSolution,
                gamePlaying = viewModel.gamePlaying,
                showMenu = viewModel.showMenu,
                mistakesCount = viewModel.mistakesCount,
                giveUp = viewModel.giveUp,
                restartButtonAnimation = restartButtonAnimation,
                onNavigateBack = { navigator.popBackStack() },
                onToggleSolution = { viewModel.showSolution = !viewModel.showSolution },
                onPlayPause = {
                    if (!viewModel.gamePlaying) viewModel.startTimer() else viewModel.pauseTimer()
                    viewModel.currCell = Cell(-1, -1, 0)
                },
                onRestartClick = { viewModel.restartDialog = true },
                onMenuClick = { viewModel.showMenu = !viewModel.showMenu },
                onMenuDismiss = { viewModel.showMenu = false },
                onGiveUpClick = {
                    viewModel.pauseTimer()
                    viewModel.giveUpDialog = true
                },
                onSettingsClick = {
                    navigator.navigate(
                        SettingsCategoriesScreenDestination(launchedFromGame = true)
                    )
                    viewModel.showMenu = false
                },
                onExportClick = {
                    val stringBoard = SudokuParser().boardToString(
                        viewModel.gameBoard,
                        emptySeparator = '.'
                    )
                    snackbarScope.launch {
                        clipboardManager.setClipEntry(
                            ClipEntry(ClipData.newPlainText("sudoku", stringBoard.uppercase()))
                        )
                    }
                    if (SDK_INT < 33) {
                        Toast.makeText(
                            context,
                            R.string.export_string_state_copied,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onSolveClick = { viewModel.solvePuzzle() }
            )
        }
    ) { scaffoldPaddings ->
        val scrollState = rememberScrollState()
        Column(
            modifier =
            Modifier
                .padding(scaffoldPaddings)
                .padding(horizontal = 12.dp)
                .then(
                    if (viewModel.endGame) Modifier.verticalScroll(scrollState) else Modifier
                ),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            AnimatedVisibility(visible = !viewModel.endGame) {
                GameInfoRow(
                    difficulty = stringResource(viewModel.gameDifficulty.resName),
                    mistakesLimit = mistakesLimit,
                    errorHighlight = errorHighlight,
                    mistakesCount = viewModel.mistakesCount,
                    timerEnabled = timerEnabled,
                    endGame = viewModel.endGame,
                    timeText = viewModel.timeText
                )
            }

            var renderNotes by remember { mutableStateOf(true) }

            GameBoardSection(
                gamePlaying = viewModel.gamePlaying,
                endGame = viewModel.endGame,
                showSolution = viewModel.showSolution,
                gameBoard = viewModel.gameBoard,
                solvedBoard = viewModel.solvedBoard,
                size = viewModel.size,
                fontSizeValue = fontSizeValue,
                fontSizeFactor = fontSizeFactor,
                notes = viewModel.notes,
                currCell = viewModel.currCell,
                highlightIdentical = highlightIdentical,
                errorHighlight = errorHighlight,
                positionLines = positionLines,
                crossHighlight = crossHighlight,
                digitFirstNumber = viewModel.digitFirstNumber,
                gameType = viewModel.gameType,
                cages = viewModel.cages,
                advancedHintMode = advancedHintMode,
                advancedHintData = advancedHintData,
                renderNotes = renderNotes,
                onClick = { cell ->
                    viewModel.processInput(cell = cell, remainingUse = remainingUse)
                    if (!viewModel.gamePlaying) {
                        localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        viewModel.startTimer()
                    }
                },
                onLongClick = { cell ->
                    if (viewModel.processInput(cell, remainingUse, longTap = true)) {
                        localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                }
            )

            AnimatedContent(advancedHintMode) { targetState ->
                if (targetState) {
                    advancedHintData?.let { hintData ->
                        AdvancedHintContainer(
                            advancedHintData = hintData,
                            onApplyClick = {
                                viewModel.applyAdvancedHint()
                            },
                            onBackClick = {
                                viewModel.cancelAdvancedHint()
                            },
                            onSettingsClick = {
                                navigator.navigate(
                                    SettingsAdvancedHintScreenDestination
                                )
                            }
                        )
                    }
                    if (advancedHintData == null) {
                        AdvancedHintContainer(
                            advancedHintData =
                            AdvancedHintData(
                                titleRes = R.string.advanced_hint_no_hint_title,
                                textResWithArg =
                                Pair(
                                    R.string.advanced_hint_no_hint,
                                    emptyList()
                                ),
                                targetCell = Cell(-1, -1, 0),
                                helpCells = emptyList()
                            ),
                            onApplyClick = null,
                            onBackClick = {
                                viewModel.cancelAdvancedHint()
                            },
                            onSettingsClick = {
                                navigator.navigate(
                                    SettingsAdvancedHintScreenDestination
                                )
                            }
                        )
                    }
                } else {
                    AnimatedContent(!viewModel.endGame, label = "") { contentState ->
                        if (contentState) {
                            Column(
                                verticalArrangement = if (funKeyboardOverNum) ReverseArrangement else Arrangement.Top
                            ) {
                                DefaultGameKeyboard(
                                    size = viewModel.size,
                                    remainingUses = if (remainingUse) viewModel.remainingUsesList else null,
                                    onClick = { viewModel.processInputKeyboard(number = it) },
                                    onLongClick = {
                                        viewModel.processInputKeyboard(
                                            number = it,
                                            longTap = true
                                        )
                                    },
                                    selected = viewModel.digitFirstNumber,
                                    isDarkTheme = resolvedDarkTheme
                                )
                                GameToolbar(
                                    showUndoRedoMenu = viewModel.showUndoRedoMenu,
                                    showNotesMenu = viewModel.showNotesMenu,
                                    notesToggled = viewModel.notesToggled,
                                    eraseButtonToggled = viewModel.eraseButtonToggled,
                                    hintsDisabled = hintsDisabled,
                                    hintsRemaining = hintsRemaining,
                                    gamePlaying = viewModel.gamePlaying,
                                    renderNotes = renderNotes,
                                    onUndoClick = { viewModel.toolbarClick(ToolBarItem.Undo) },
                                    onUndoLongClick = { viewModel.showUndoRedoMenu = true },
                                    onUndoRedoMenuDismiss = { viewModel.showUndoRedoMenu = false },
                                    onRedoClick = { viewModel.toolbarClick(ToolBarItem.Redo) },
                                    onHintClick = { viewModel.toolbarClick(ToolBarItem.Hint) },
                                    onNoteClick = { viewModel.toolbarClick(ToolBarItem.Note) },
                                    onNoteLongClick = { viewModel.showNotesMenu = true },
                                    onNotesMenuDismiss = { viewModel.showNotesMenu = false },
                                    onComputeNotesClick = { viewModel.computeNotes() },
                                    onClearNotesClick = { viewModel.clearNotes() },
                                    onRenderNotesClick = { renderNotes = !renderNotes },
                                    onEraseClick = { viewModel.toolbarClick(ToolBarItem.Remove) },
                                    onEraseLongClick = { viewModel.toggleEraseButton() }
                                )
                            }
                        } else {
                            // Game completed section
                            val allRecords by viewModel.allRecords.collectAsStateWithLifecycle(
                                initialValue = emptyList()
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Show XP earned if game was completed successfully
                                if (!viewModel.giveUp && viewModel.mistakesCount < PreferencesConstants.MISTAKES_LIMIT) {
                                    xpResult?.let { result ->
                                        XPEarnedDisplay(
                                            xpResult = result,
                                            modifier = Modifier.padding(horizontal = 4.dp)
                                        )
                                    }
                                }

                                AfterGameStats(
                                    modifier = Modifier.fillMaxWidth(),
                                    difficulty = viewModel.gameDifficulty,
                                    type = viewModel.gameType,
                                    hintsUsed = viewModel.hintsUsed,
                                    mistakesMade = viewModel.mistakesMade,
                                    mistakesLimit = mistakesLimit,
                                    mistakesLimitCount = viewModel.mistakesCount,
                                    giveUp = viewModel.giveUp,
                                    notesTaken = viewModel.notesTaken,
                                    records = allRecords,
                                    timeText = viewModel.timeText
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // dialogs
    if (viewModel.restartDialog) {
        viewModel.pauseTimer()
        RestartGameDialog(
            onConfirm = {
                restartButtonAngleState -= 360
                viewModel.resetGame(resetTimer)
                viewModel.restartDialog = false
                viewModel.startTimer()
            },
            onDismiss = {
                viewModel.restartDialog = false
                viewModel.startTimer()
            }
        )
    } else if (viewModel.giveUpDialog) {
        viewModel.pauseTimer()
        GiveUpDialog(
            onConfirm = {
                viewModel.giveUp()
                viewModel.giveUpDialog = false
                viewModel.pauseTimer()
            },
            onDismiss = {
                viewModel.giveUpDialog = false
                viewModel.startTimer()
            }
        )
    }

    // Achievement unlock dialog
    if (unlockedAchievements.isNotEmpty()) {
        AchievementUnlockDialog(
            achievements = unlockedAchievements,
            onDismiss = { unlockedAchievements = emptyList() }
        )
    }

    // Level up dialog
    showLevelUpDialog?.let { newLevel ->
        LevelUpDialog(
            newLevel = newLevel,
            onDismiss = { showLevelUpDialog = null }
        )
    }

    LaunchedEffect(viewModel.mistakesMethod) {
        viewModel.checkMistakesAll()
    }

    LaunchedEffect(Unit) {
        if (!viewModel.endGame && !viewModel.gameCompleted) {
            viewModel.startTimer()
        }
    }

    LaunchedEffect(viewModel.gameCompleted) {
        if (viewModel.gameCompleted) {
            viewModel.onGameComplete()
        }
    }

    // so that the timer doesn't run in the background
    // https://stackoverflow.com/questions/66546962/jetpack-compose-how-do-i-refresh-a-screen-when-app-returns-to-foreground/66807899#66807899
    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (viewModel.gamePlaying) viewModel.startTimer()
            }

            Lifecycle.Event.ON_PAUSE -> {
                viewModel.pauseTimer()
                viewModel.currCell = Cell(-1, -1, 0)
            }

            Lifecycle.Event.ON_DESTROY -> viewModel.pauseTimer()
            else -> {}
        }
    }
}

@Composable
fun TopBoardSection(text: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameTopAppBar(
    endGame: Boolean,
    showSolution: Boolean,
    gamePlaying: Boolean,
    showMenu: Boolean,
    mistakesCount: Int,
    giveUp: Boolean,
    restartButtonAnimation: Float,
    onNavigateBack: () -> Unit,
    onToggleSolution: () -> Unit,
    onPlayPause: () -> Unit,
    onRestartClick: () -> Unit,
    onMenuClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    onGiveUpClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExportClick: () -> Unit,
    onSolveClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_round_arrow_back_24),
                    contentDescription = null
                )
            }
        },
        actions = {
            AnimatedVisibility(
                visible = endGame && (mistakesCount >= PreferencesConstants.MISTAKES_LIMIT || giveUp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(onClick = onToggleSolution) {
                        AnimatedContent(
                            if (showSolution) {
                                stringResource(R.string.action_show_mine_sudoku)
                            } else {
                                stringResource(R.string.action_show_solution)
                            },
                            label = "Show solution/mine button"
                        ) {
                            Text(it)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !endGame) {
                val rotationAngle by animateFloatAsState(
                    targetValue = if (gamePlaying) 0f else 360f,
                    label = "Play/Pause game icon rotation"
                )
                IconButton(onClick = onPlayPause) {
                    Icon(
                        modifier = Modifier.rotate(rotationAngle),
                        painter = painterResource(
                            if (gamePlaying) R.drawable.ic_round_pause_24 else R.drawable.ic_round_play_24
                        ),
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = !endGame) {
                IconButton(onClick = onRestartClick) {
                    Icon(
                        modifier = Modifier.rotate(restartButtonAnimation),
                        painter = painterResource(R.drawable.ic_round_replay_24),
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = !endGame) {
                Box {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    GameMenu(
                        expanded = showMenu,
                        onDismiss = onMenuDismiss,
                        onGiveUpClick = onGiveUpClick,
                        onSettingsClick = onSettingsClick,
                        onExportClick = onExportClick,
                        onSolveClick = onSolveClick
                    )
                }
            }
        }
    )
}

@Composable
private fun GameInfoRow(
    difficulty: String,
    mistakesLimit: Boolean,
    errorHighlight: Int,
    mistakesCount: Int,
    timerEnabled: Boolean,
    endGame: Boolean,
    timeText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TopBoardSection(difficulty)

        if (mistakesLimit && errorHighlight != 0) {
            TopBoardSection(
                stringResource(R.string.mistakes_number_out_of, mistakesCount, 3)
            )
        }

        AnimatedVisibility(visible = timerEnabled || endGame) {
            TopBoardSection(timeText)
        }
    }
}

@Composable
private fun GameBoardSection(
    modifier: Modifier = Modifier,
    gamePlaying: Boolean,
    endGame: Boolean,
    showSolution: Boolean,
    gameBoard: List<List<Cell>>,
    solvedBoard: List<List<Cell>>,
    size: Int,
    fontSizeValue: TextUnit,
    fontSizeFactor: Int,
    notes: List<Note>,
    currCell: Cell,
    highlightIdentical: Boolean,
    errorHighlight: Int,
    positionLines: Boolean,
    crossHighlight: Boolean,
    digitFirstNumber: Int,
    gameType: GameType,
    cages: List<Cage>,
    advancedHintMode: Boolean,
    advancedHintData: AdvancedHintData?,
    renderNotes: Boolean,
    onClick: (Cell) -> Unit,
    onLongClick: (Cell) -> Unit
) {
    val boardBlur by animateDpAsState(
        targetValue = if (gamePlaying || endGame) 0.dp else 10.dp,
        label = "Game board blur"
    )
    val boardScale by animateFloatAsState(
        targetValue = if (gamePlaying || endGame) 1f else 0.90f,
        label = "Game board scale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            AnimatedVisibility(
                visible = !gamePlaying && !endGame,
                enter = expandVertically(clip = false) + fadeIn(),
                exit = shrinkVertically(clip = false) + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(12.dp)
                )
            }
        }
        Board(
            modifier = Modifier
                .blur(boardBlur)
                .scale(boardScale, boardScale),
            board = if (!showSolution) gameBoard else solvedBoard,
            size = size,
            mainTextSize = fontSizeValue,
            autoFontSize = fontSizeFactor == 0,
            notes = notes,
            selectedCell = currCell,
            onClick = onClick,
            onLongClick = onLongClick,
            identicalNumbersHighlight = highlightIdentical,
            errorsHighlight = errorHighlight != 0,
            positionLines = positionLines,
            notesToHighlight = if (digitFirstNumber > 0) {
                notes.filter { it.value == digitFirstNumber }
            } else {
                emptyList()
            },
            enabled = gamePlaying && !endGame,
            questions = !(gamePlaying || endGame) && SDK_INT < Build.VERSION_CODES.R,
            renderNotes = renderNotes && !showSolution,
            zoomable = gameType == GameType.Default12x12 || gameType == GameType.Killer12x12,
            crossHighlight = crossHighlight,
            cages = cages,
            cellsToHighlight = if (advancedHintMode && advancedHintData != null) {
                advancedHintData.helpCells + advancedHintData.targetCell
            } else {
                null
            }
        )
    }
}

@Composable
private fun GameToolbar(
    modifier: Modifier = Modifier,
    showUndoRedoMenu: Boolean,
    showNotesMenu: Boolean,
    notesToggled: Boolean,
    eraseButtonToggled: Boolean,
    hintsDisabled: Boolean,
    hintsRemaining: Int,
    gamePlaying: Boolean,
    renderNotes: Boolean,
    onUndoClick: () -> Unit,
    onUndoLongClick: () -> Unit,
    onUndoRedoMenuDismiss: () -> Unit,
    onRedoClick: () -> Unit,
    onHintClick: () -> Unit,
    onNoteClick: () -> Unit,
    onNoteLongClick: () -> Unit,
    onNotesMenuDismiss: () -> Unit,
    onComputeNotesClick: () -> Unit,
    onClearNotesClick: () -> Unit,
    onRenderNotesClick: () -> Unit,
    onEraseClick: () -> Unit,
    onEraseLongClick: () -> Unit
) {
    val localView = LocalView.current

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(ToolbarItemHeight)
        ) {
            UndoRedoMenu(
                expanded = showUndoRedoMenu,
                onDismiss = onUndoRedoMenuDismiss,
                onRedoClick = onRedoClick
            )
            ToolbarItem(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.ic_round_undo_24),
                contentDescription = stringResource(R.string.action_undo),
                onClick = onUndoClick,
                onLongClick = onUndoLongClick
            )
        }

        if (!hintsDisabled) {
            ToolbarItem(
                modifier = Modifier
                    .weight(1f)
                    .height(ToolbarItemHeight),
                painter = painterResource(R.drawable.ic_lightbulb_stars_24),
                contentDescription = stringResource(R.string.action_hint),
                enabled = true,
                visualEnabled = hintsRemaining > 0,
                badgeText = hintsRemaining.toString(),
                onClick = onHintClick
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(ToolbarItemHeight)
        ) {
            NotesMenu(
                expanded = showNotesMenu,
                onDismiss = onNotesMenuDismiss,
                onComputeNotesClick = onComputeNotesClick,
                onClearNotesClick = onClearNotesClick,
                renderNotes = renderNotes,
                onRenderNotesClick = onRenderNotesClick
            )
            ToolbarItem(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.ic_round_edit_24),
                contentDescription = stringResource(R.string.action_notes),
                toggled = notesToggled,
                onClick = onNoteClick,
                onLongClick = {
                    if (gamePlaying) {
                        localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        onNoteLongClick()
                    }
                }
            )
        }

        ToolbarItem(
            modifier = Modifier
                .weight(1f)
                .height(ToolbarItemHeight),
            painter = painterResource(R.drawable.ic_eraser_24),
            contentDescription = stringResource(R.string.action_erase),
            toggled = eraseButtonToggled,
            onClick = onEraseClick,
            onLongClick = {
                if (gamePlaying) {
                    localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onEraseLongClick()
                }
            }
        )
    }
}

@Composable
private fun RewardedHintDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        title = { Text(stringResource(R.string.hints_ad_title)) },
        text = { Text(stringResource(R.string.hints_ad_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.hints_ad_watch))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.hints_ad_cancel))
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
private fun RestartGameDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        title = { Text(stringResource(R.string.action_reset_game)) },
        text = { Text(stringResource(R.string.reset_game_text)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_no))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_yes))
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
private fun GiveUpDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        title = { Text(stringResource(R.string.action_give_up)) },
        text = { Text(stringResource(R.string.give_up_text)) },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_no))
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.dialog_yes))
            }
        },
        onDismissRequest = onDismiss
    )
}

@Composable
fun OnLifecycleEvent(onEvent: (owner: LifecycleOwner, event: Lifecycle.Event) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        val observer =
            LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, event)
            }

        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun KeepScreenOn() = AndroidView({ View(it).apply { keepScreenOn = true } })

// region Previews

private fun createSampleBoard(): List<List<Cell>> {
    val puzzle = listOf(
        listOf(5, 3, 0, 0, 7, 0, 0, 0, 0),
        listOf(6, 0, 0, 1, 9, 5, 0, 0, 0),
        listOf(0, 9, 8, 0, 0, 0, 0, 6, 0),
        listOf(8, 0, 0, 0, 6, 0, 0, 0, 3),
        listOf(4, 0, 0, 8, 0, 3, 0, 0, 1),
        listOf(7, 0, 0, 0, 2, 0, 0, 0, 6),
        listOf(0, 6, 0, 0, 0, 0, 2, 8, 0),
        listOf(0, 0, 0, 4, 1, 9, 0, 0, 5),
        listOf(0, 0, 0, 0, 8, 0, 0, 7, 9)
    )
    return puzzle.mapIndexed { row, cols ->
        cols.mapIndexed { col, value ->
            Cell(row, col, value, locked = value != 0)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GameTopAppBarPreview() {
    SudokuPlusTheme {
        GameTopAppBar(
            endGame = false,
            showSolution = false,
            gamePlaying = true,
            showMenu = false,
            mistakesCount = 1,
            giveUp = false,
            restartButtonAnimation = 0f,
            onNavigateBack = {},
            onToggleSolution = {},
            onPlayPause = {},
            onRestartClick = {},
            onMenuClick = {},
            onMenuDismiss = {},
            onGiveUpClick = {},
            onSettingsClick = {},
            onExportClick = {},
            onSolveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameInfoRowPreview() {
    SudokuPlusTheme {
        GameInfoRow(
            difficulty = "Easy",
            mistakesLimit = true,
            errorHighlight = 2,
            mistakesCount = 1,
            timerEnabled = true,
            endGame = false,
            timeText = "05:23"
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameBoardSectionPreview() {
    SudokuPlusTheme {
        GameBoardSection(
            gamePlaying = true,
            endGame = false,
            showSolution = false,
            gameBoard = createSampleBoard(),
            solvedBoard = createSampleBoard(),
            size = 9,
            fontSizeValue = 26.sp,
            fontSizeFactor = 0,
            notes = emptyList(),
            currCell = Cell(2, 2, 0),
            highlightIdentical = true,
            errorHighlight = 2,
            positionLines = true,
            crossHighlight = false,
            digitFirstNumber = 0,
            gameType = GameType.Default9x9,
            cages = emptyList(),
            advancedHintMode = false,
            advancedHintData = null,
            renderNotes = true,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameToolbarPreview() {
    SudokuPlusTheme {
        GameToolbar(
            showUndoRedoMenu = false,
            showNotesMenu = false,
            notesToggled = false,
            eraseButtonToggled = false,
            hintsDisabled = false,
            hintsRemaining = 3,
            gamePlaying = true,
            renderNotes = true,
            onUndoClick = {},
            onUndoLongClick = {},
            onUndoRedoMenuDismiss = {},
            onRedoClick = {},
            onHintClick = {},
            onNoteClick = {},
            onNoteLongClick = {},
            onNotesMenuDismiss = {},
            onComputeNotesClick = {},
            onClearNotesClick = {},
            onRenderNotesClick = {},
            onEraseClick = {},
            onEraseLongClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GameScreenContentPreview() {
    SudokuPlusTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GameInfoRow(
                difficulty = "Medium",
                mistakesLimit = true,
                errorHighlight = 2,
                mistakesCount = 0,
                timerEnabled = true,
                endGame = false,
                timeText = "02:45"
            )

            GameBoardSection(
                gamePlaying = true,
                endGame = false,
                showSolution = false,
                gameBoard = createSampleBoard(),
                solvedBoard = createSampleBoard(),
                size = 9,
                fontSizeValue = 26.sp,
                fontSizeFactor = 0,
                notes = emptyList(),
                currCell = Cell(4, 4, 0),
                highlightIdentical = true,
                errorHighlight = 2,
                positionLines = true,
                crossHighlight = false,
                digitFirstNumber = 0,
                gameType = GameType.Default9x9,
                cages = emptyList(),
                advancedHintMode = false,
                advancedHintData = null,
                renderNotes = true,
                onClick = {},
                onLongClick = {}
            )

            GameToolbar(
                showUndoRedoMenu = false,
                showNotesMenu = false,
                notesToggled = false,
                eraseButtonToggled = false,
                hintsDisabled = false,
                hintsRemaining = 2,
                gamePlaying = true,
                renderNotes = true,
                onUndoClick = {},
                onUndoLongClick = {},
                onUndoRedoMenuDismiss = {},
                onRedoClick = {},
                onHintClick = {},
                onNoteClick = {},
                onNoteLongClick = {},
                onNotesMenuDismiss = {},
                onComputeNotesClick = {},
                onClearNotesClick = {},
                onRenderNotesClick = {},
                onEraseClick = {},
                onEraseLongClick = {}
            )
        }
    }
}

// endregion
