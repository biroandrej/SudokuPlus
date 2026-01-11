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
import androidx.compose.material.icons.rounded.AutoAwesome
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import sk.awisoft.sudokuplus.core.PreferencesConstants
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

    val boardBlur by animateDpAsState(
        targetValue = if (viewModel.gamePlaying || viewModel.endGame) 0.dp else 10.dp,
        label = "Game board blur"
    )
    val boardScale by animateFloatAsState(
        targetValue = if (viewModel.gamePlaying || viewModel.endGame) 1f else 0.90f,
        label = "Game board scale"
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
        AlertDialog(
            title = { Text(stringResource(R.string.hints_ad_title)) },
            text = { Text(stringResource(R.string.hints_ad_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRewardedHintDialog = false
                        val activity = context.findActivity()
                        if (activity == null) {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.hints_no_remaining)
                                )
                            }
                            return@TextButton
                        }
                        val shown =
                            AdsManager.showRewardedIfAvailable(activity) {
                                viewModel.applyRewardedHint()
                            }
                        if (!shown) {
                            snackbarScope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.hints_no_remaining)
                                )
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.hints_ad_watch))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRewardedHintDialog = false }) {
                    Text(stringResource(R.string.hints_ad_cancel))
                }
            },
            onDismissRequest = { showRewardedHintDialog = false }
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
                viewModel.requestAIHint()
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
                        viewModel.grantAIHintFromAd()
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
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navigator.popBackStack() }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_round_arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = viewModel.endGame && (viewModel.mistakesCount >= PreferencesConstants.Companion.MISTAKES_LIMIT || viewModel.giveUp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { viewModel.showSolution = !viewModel.showSolution }
                            ) {
                                AnimatedContent(
                                    if (viewModel.showSolution) {
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

                    AnimatedVisibility(visible = !viewModel.endGame) {
                        val rotationAngle by animateFloatAsState(
                            targetValue = if (viewModel.gamePlaying) 0f else 360f,
                            label = "Play/Pause game icon rotation"
                        )
                        IconButton(onClick = {
                            if (!viewModel.gamePlaying) viewModel.startTimer() else viewModel.pauseTimer()
                            viewModel.currCell = Cell(-1, -1, 0)
                        }) {
                            Icon(
                                modifier = Modifier.rotate(rotationAngle),
                                painter =
                                painterResource(
                                    if (viewModel.gamePlaying) {
                                        R.drawable.ic_round_pause_24
                                    } else {
                                        R.drawable.ic_round_play_24
                                    }
                                ),
                                contentDescription = null
                            )
                        }
                    }

                    AnimatedVisibility(visible = !viewModel.endGame) {
                        IconButton(onClick = { viewModel.restartDialog = true }) {
                            Icon(
                                modifier = Modifier.rotate(restartButtonAnimation),
                                painter = painterResource(R.drawable.ic_round_replay_24),
                                contentDescription = null
                            )
                        }
                    }
                    AnimatedVisibility(visible = !viewModel.endGame) {
                        Box {
                            IconButton(onClick = { viewModel.showMenu = !viewModel.showMenu }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = null
                                )
                            }
                            GameMenu(
                                expanded = viewModel.showMenu,
                                onDismiss = { viewModel.showMenu = false },
                                onGiveUpClick = {
                                    viewModel.pauseTimer()
                                    viewModel.giveUpDialog = true
                                },
                                onSettingsClick = {
                                    navigator.navigate(
                                        SettingsCategoriesScreenDestination(
                                            launchedFromGame = true
                                        )
                                    )
                                    viewModel.showMenu = false
                                },
                                onExportClick = {
                                    val stringBoard =
                                        SudokuParser().boardToString(
                                            viewModel.gameBoard,
                                            emptySeparator = '.'
                                        )
                                    snackbarScope.launch {
                                        clipboardManager.setClipEntry(
                                            ClipEntry(
                                                ClipData.newPlainText(
                                                    "sudoku",
                                                    stringBoard.uppercase()
                                                )
                                            )
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
                    }
                }
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
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TopBoardSection(stringResource(viewModel.gameDifficulty.resName))

                    if (mistakesLimit && errorHighlight != 0) {
                        TopBoardSection(
                            stringResource(
                                R.string.mistakes_number_out_of,
                                viewModel.mistakesCount,
                                3
                            )
                        )
                    }

                    val timerEnabled by viewModel.timerEnabled.collectAsStateWithLifecycle(
                        initialValue = PreferencesConstants.Companion.DEFAULT_SHOW_TIMER
                    )
                    AnimatedVisibility(visible = timerEnabled || viewModel.endGame) {
                        TopBoardSection(viewModel.timeText)
                    }
                }
            }

            var renderNotes by remember { mutableStateOf(true) }

            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    AnimatedVisibility(
                        visible = !viewModel.gamePlaying && !viewModel.endGame,
                        enter = expandVertically(clip = false) + fadeIn(),
                        exit = shrinkVertically(clip = false) + fadeOut()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayCircle,
                            contentDescription = null,
                            modifier =
                            Modifier
                                .size(48.dp)
                                .shadow(12.dp)
                        )
                    }
                }
                Board(
                    modifier =
                    Modifier
                        .blur(boardBlur)
                        .scale(boardScale, boardScale),
                    board = if (!viewModel.showSolution) viewModel.gameBoard else viewModel.solvedBoard,
                    size = viewModel.size,
                    mainTextSize = fontSizeValue,
                    autoFontSize = fontSizeFactor == 0,
                    notes = viewModel.notes,
                    selectedCell = viewModel.currCell,
                    onClick = { cell ->
                        viewModel.processInput(
                            cell = cell,
                            remainingUse = remainingUse
                        )
                        if (!viewModel.gamePlaying) {
                            localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            viewModel.startTimer()
                        }
                    },
                    onLongClick = { cell ->
                        if (viewModel.processInput(cell, remainingUse, longTap = true)) {
                            localView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        }
                    },
                    identicalNumbersHighlight = highlightIdentical,
                    errorsHighlight = errorHighlight != 0,
                    positionLines = positionLines,
                    notesToHighlight =
                    if (viewModel.digitFirstNumber > 0) {
                        viewModel.notes.filter { it.value == viewModel.digitFirstNumber }
                    } else {
                        emptyList()
                    },
                    enabled = viewModel.gamePlaying && !viewModel.endGame,
                    questions = !(viewModel.gamePlaying || viewModel.endGame) && SDK_INT < Build.VERSION_CODES.R,
                    renderNotes = renderNotes && !viewModel.showSolution,
                    zoomable = viewModel.gameType == GameType.Default12x12 || viewModel.gameType == GameType.Killer12x12,
                    crossHighlight = crossHighlight,
                    cages = viewModel.cages,
                    cellsToHighlight = if (advancedHintMode && advancedHintData != null) advancedHintData!!.helpCells + advancedHintData!!.targetCell else null
                )
            }

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
                                    onClick = {
                                        viewModel.processInputKeyboard(number = it)
                                    },
                                    onLongClick = {
                                        viewModel.processInputKeyboard(
                                            number = it,
                                            longTap = true
                                        )
                                    },
                                    selected = viewModel.digitFirstNumber,
                                    isDarkTheme = resolvedDarkTheme
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Box(
                                        modifier =
                                        Modifier
                                            .weight(1f)
                                            .height(ToolbarItemHeight)
                                    ) {
                                        UndoRedoMenu(
                                            expanded = viewModel.showUndoRedoMenu,
                                            onDismiss = { viewModel.showUndoRedoMenu = false },
                                            onRedoClick = {
                                                viewModel.toolbarClick(
                                                    ToolBarItem.Redo
                                                )
                                            }
                                        )
                                        ToolbarItem(
                                            modifier = Modifier.fillMaxSize(),
                                            painter = painterResource(R.drawable.ic_round_undo_24),
                                            contentDescription = stringResource(
                                                R.string.action_undo
                                            ),
                                            onClick = { viewModel.toolbarClick(ToolBarItem.Undo) },
                                            onLongClick = { viewModel.showUndoRedoMenu = true }
                                        )
                                    }
                                    val hintsDisabled by viewModel.disableHints.collectAsStateWithLifecycle(
                                        initialValue = PreferencesConstants.Companion.DEFAULT_HINTS_DISABLED
                                    )
                                    val hintsRemaining by viewModel.hintsRemaining.collectAsStateWithLifecycle(
                                        initialValue = PreferencesConstants.DEFAULT_HINTS_PER_GAME
                                    )
                                    if (!hintsDisabled) {
                                        ToolbarItem(
                                            modifier =
                                            Modifier
                                                .weight(1f)
                                                .height(ToolbarItemHeight),
                                            painter = painterResource(
                                                R.drawable.ic_lightbulb_stars_24
                                            ),
                                            contentDescription = stringResource(
                                                R.string.action_hint
                                            ),
                                            enabled = true,
                                            visualEnabled = hintsRemaining > 0,
                                            badgeText = hintsRemaining.toString(),
                                            onClick = { viewModel.toolbarClick(ToolBarItem.Hint) }
                                        )
                                    }

                                    Box(
                                        modifier =
                                        Modifier
                                            .weight(1f)
                                            .height(ToolbarItemHeight)
                                    ) {
                                        NotesMenu(
                                            expanded = viewModel.showNotesMenu,
                                            onDismiss = { viewModel.showNotesMenu = false },
                                            onComputeNotesClick = { viewModel.computeNotes() },
                                            onClearNotesClick = { viewModel.clearNotes() },
                                            renderNotes = renderNotes,
                                            onRenderNotesClick = { renderNotes = !renderNotes }
                                        )
                                        ToolbarItem(
                                            modifier = Modifier.fillMaxSize(),
                                            painter = painterResource(R.drawable.ic_round_edit_24),
                                            contentDescription = stringResource(
                                                R.string.action_notes
                                            ),
                                            toggled = viewModel.notesToggled,
                                            onClick = { viewModel.toolbarClick(ToolBarItem.Note) },
                                            onLongClick = {
                                                if (viewModel.gamePlaying) {
                                                    localView.performHapticFeedback(
                                                        HapticFeedbackConstants.VIRTUAL_KEY
                                                    )
                                                    viewModel.showNotesMenu = true
                                                }
                                            }
                                        )
                                    }
                                    ToolbarItem(
                                        modifier =
                                        Modifier
                                            .weight(1f)
                                            .height(ToolbarItemHeight),
                                        painter = painterResource(R.drawable.ic_eraser_24),
                                        contentDescription = stringResource(R.string.action_erase),
                                        toggled = viewModel.eraseButtonToggled,
                                        onClick = {
                                            viewModel.toolbarClick(ToolBarItem.Remove)
                                        },
                                        onLongClick = {
                                            if (viewModel.gamePlaying) {
                                                localView.performHapticFeedback(
                                                    HapticFeedbackConstants.VIRTUAL_KEY
                                                )
                                                viewModel.toggleEraseButton()
                                            }
                                        }
                                    )
                                    if (advancedHintEnabled) {
                                        ToolbarItem(
                                            modifier =
                                            Modifier
                                                .weight(1f)
                                                .height(ToolbarItemHeight),
                                            painter = rememberVectorPainter(
                                                Icons.Rounded.AutoAwesome
                                            ),
                                            contentDescription = stringResource(
                                                R.string.action_advanced_hint
                                            ),
                                            onClick = {
                                                if (viewModel.gamePlaying) {
                                                    viewModel.getAdvancedHint()
                                                }
                                            }
                                        )
                                    }
                                }
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
        AlertDialog(
            title = { Text(stringResource(R.string.action_reset_game)) },
            text = { Text(stringResource(R.string.reset_game_text)) },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.restartDialog = false
                    viewModel.startTimer()
                }) {
                    Text(stringResource(R.string.dialog_no))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    restartButtonAngleState -= 360
                    viewModel.resetGame(resetTimer)
                    viewModel.restartDialog = false
                    viewModel.startTimer()
                }) {
                    Text(stringResource(R.string.dialog_yes))
                }
            },
            onDismissRequest = {
                viewModel.restartDialog = false
                viewModel.startTimer()
            }
        )
    } else if (viewModel.giveUpDialog) {
        viewModel.pauseTimer()
        AlertDialog(
            title = { Text(stringResource(R.string.action_give_up)) },
            text = { Text(stringResource(R.string.give_up_text)) },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.giveUpDialog = false
                    viewModel.startTimer()
                }) {
                    Text(stringResource(R.string.dialog_no))
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.giveUp()
                    viewModel.giveUpDialog = false
                    viewModel.pauseTimer()
                }) {
                    Text(stringResource(R.string.dialog_yes))
                }
            },
            onDismissRequest = {
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
