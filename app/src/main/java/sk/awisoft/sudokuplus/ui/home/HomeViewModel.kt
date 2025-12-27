package sk.awisoft.sudokuplus.ui.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.awisoft.sudokuplus.core.Cell
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.core.notification.DailyChallengeNotificationWorker
import sk.awisoft.sudokuplus.core.notification.NotificationHelper
import sk.awisoft.sudokuplus.core.notification.StreakReminderWorker
import sk.awisoft.sudokuplus.core.qqwing.Cage
import sk.awisoft.sudokuplus.core.qqwing.CageGenerator
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.qqwing.QQWingController
import sk.awisoft.sudokuplus.core.reward.ClaimResult
import sk.awisoft.sudokuplus.core.reward.DailyReward
import sk.awisoft.sudokuplus.core.reward.RewardCalendarManager
import sk.awisoft.sudokuplus.core.reward.RewardCalendarState
import sk.awisoft.sudokuplus.core.utils.SudokuParser
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import sk.awisoft.sudokuplus.data.datastore.PlayGamesSettingsManager
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.playgames.PlayGamesManager
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    dailyChallengeRepository: DailyChallengeRepository,
    private val appSettingsManager: AppSettingsManager,
    private val boardRepository: BoardRepository,
    private val savedGameRepository: SavedGameRepository,
    private val dailyChallengeManager: DailyChallengeManager,
    private val notificationSettingsManager: NotificationSettingsManager,
    private val notificationHelper: NotificationHelper,
    private val rewardCalendarManager: RewardCalendarManager,
    private val playGamesSettingsManager: PlayGamesSettingsManager,
    private val playGamesManager: PlayGamesManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    val lastSavedGame =
        savedGameRepository.getLast()
            .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    // Daily Challenge StateO
    private val _dailyChallenge = MutableStateFlow<DailyChallenge?>(null)
    val dailyChallenge: StateFlow<DailyChallenge?> = _dailyChallenge.asStateFlow()

    private val _isDailyLoading = MutableStateFlow(false)
    val isDailyLoading: StateFlow<Boolean> = _isDailyLoading.asStateFlow()

    val dailyCurrentStreak: StateFlow<Int> =
        dailyChallengeRepository.getCompleted()
            .map { challenges ->
                dailyChallengeManager.calculateCurrentStreak(challenges.map { it.date })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    var dailyChallengeGameUid = -1L
    var dailyChallengeReadyToPlay by mutableStateOf(false)

    // Notification permission state
    private val _shouldShowNotificationPermission = MutableStateFlow(false)
    val shouldShowNotificationPermission: StateFlow<Boolean> = _shouldShowNotificationPermission.asStateFlow()

    // Reward Calendar State
    val rewardCalendarState: StateFlow<RewardCalendarState?> =
        rewardCalendarManager.getCalendarState()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _claimedReward = MutableStateFlow<DailyReward?>(null)
    val claimedReward: StateFlow<DailyReward?> = _claimedReward.asStateFlow()

    // Play Games Prompt State
    val showPlayGamesPrompt: StateFlow<Boolean> =
        playGamesSettingsManager.playGamesEnabled
            .map { enabled -> !enabled }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isPlayGamesPromptDismissed: StateFlow<Boolean> =
        playGamesSettingsManager.homePromptDismissed
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        loadDailyChallenge()
        checkNotificationPermission()
    }

    fun dismissPlayGamesPrompt() {
        viewModelScope.launch(Dispatchers.IO) {
            playGamesSettingsManager.setHomePromptDismissed(true)
        }
    }

    private fun checkNotificationPermission() {
        viewModelScope.launch(Dispatchers.IO) {
            val alreadyRequested = notificationSettingsManager.notificationPermissionRequested.first()
            val hasPermission = notificationHelper.hasNotificationPermission()

            // Show permission dialog if not already requested and don't have permission
            if (!alreadyRequested && !hasPermission) {
                _shouldShowNotificationPermission.value = true
            } else if (hasPermission) {
                // If we have permission, make sure notifications are scheduled
                scheduleNotificationsIfEnabled()
            }
        }
    }

    fun onNotificationPermissionRequested() {
        viewModelScope.launch(Dispatchers.IO) {
            notificationSettingsManager.setNotificationPermissionRequested(true)
            _shouldShowNotificationPermission.value = false
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            notificationSettingsManager.setNotificationPermissionRequested(true)
            _shouldShowNotificationPermission.value = false

            if (granted) {
                scheduleNotificationsIfEnabled()
            }
        }
    }

    private suspend fun scheduleNotificationsIfEnabled() {
        val dailyEnabled = notificationSettingsManager.dailyChallengeNotificationEnabled.first()
        val streakEnabled = notificationSettingsManager.streakReminderEnabled.first()

        if (dailyEnabled) {
            val hour = notificationSettingsManager.dailyChallengeNotificationHour.first()
            val minute = notificationSettingsManager.dailyChallengeNotificationMinute.first()
            DailyChallengeNotificationWorker.schedule(context, hour, minute)
        }

        if (streakEnabled) {
            val hour = notificationSettingsManager.streakReminderHour.first()
            val minute = notificationSettingsManager.streakReminderMinute.first()
            StreakReminderWorker.schedule(context, hour, minute)
        }
    }

    private fun loadDailyChallenge() {
        viewModelScope.launch(Dispatchers.IO) {
            _isDailyLoading.value = true
            _dailyChallenge.value = dailyChallengeManager.getOrCreateTodayChallenge()
            _isDailyLoading.value = false
        }
    }

    fun playDailyChallenge() {
        val challenge = _dailyChallenge.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            // Create a SudokuBoard from the DailyChallenge
            val board =
                SudokuBoard(
                    uid = 0,
                    initialBoard = challenge.initialBoard,
                    solvedBoard = challenge.solvedBoard,
                    difficulty = challenge.difficulty,
                    type = challenge.gameType
                )
            dailyChallengeGameUid = boardRepository.insert(board)
            withContext(Dispatchers.Main) {
                dailyChallengeReadyToPlay = true
            }
        }
    }

    private val lastGamesLimit = 5
    val lastGames =
        savedGameRepository.getLastPlayable(limit = lastGamesLimit)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    var insertedBoardUid = -1L

    private val difficulties =
        listOf(
            GameDifficulty.Easy,
            GameDifficulty.Moderate,
            GameDifficulty.Hard,
            GameDifficulty.Challenge
        )

    private val types =
        listOf(
            GameType.Default9x9,
            GameType.Default6x6,
            GameType.Default12x12,
            GameType.Killer9x9,
            GameType.Killer12x12,
            GameType.Killer6x6
        )

    val lastSelectedGameDifficultyType = appSettingsManager.lastSelectedGameDifficultyType
    val saveSelectedGameDifficultyType = appSettingsManager.saveSelectedGameDifficultyType

    var selectedDifficulty by mutableStateOf(difficulties.first())
    var selectedType by mutableStateOf(types.first())

    var isGenerating by mutableStateOf(false)
    var isSolving by mutableStateOf(false)
    var readyToPlay by mutableStateOf(false)

    private var puzzle =
        List(selectedType.size) { row -> List(selectedType.size) { col -> Cell(row, col, 0) } }
    private var solvedPuzzle =
        List(selectedType.size) { row -> List(selectedType.size) { col -> Cell(row, col, 0) } }

    fun startGame() {
        isSolving = false
        isGenerating = false

        val gameTypeToGenerate = selectedType
        val gameDifficultyToGenerate = selectedDifficulty
        val size = gameTypeToGenerate.size

        puzzle = List(size) { row -> List(size) { col -> Cell(row, col, 0) } }
        solvedPuzzle = List(size) { row -> List(size) { col -> Cell(row, col, 0) } }

        viewModelScope.launch(Dispatchers.Default) {
            val saveSelectedGameDifficultyAndType = appSettingsManager.saveSelectedGameDifficultyType.first()
            if (saveSelectedGameDifficultyAndType) {
                appSettingsManager.setLastSelectedGameDifficultyType(
                    difficulty = selectedDifficulty,
                    type = selectedType
                )
            }

            val qqWingController = QQWingController()

            // generating
            isGenerating = true
            val generated = qqWingController.generate(
                gameTypeToGenerate,
                gameDifficultyToGenerate
            )
            isGenerating = false

            isSolving = true
            val solved = qqWingController.solve(generated, gameTypeToGenerate)
            isSolving = false

            if (!qqWingController.isImpossible && qqWingController.solutionCount == 1) {
                for (i in 0 until size) {
                    for (j in 0 until size) {
                        puzzle[i][j].value = generated[i * size + j]
                        solvedPuzzle[i][j].value = solved[i * size + j]
                    }
                }

                var cages: List<Cage>? = null
                if (gameTypeToGenerate in
                    setOf(
                        GameType.Killer9x9,
                        GameType.Killer12x12,
                        GameType.Killer6x6
                    )
                ) {
                    val generator = CageGenerator(solvedPuzzle, gameTypeToGenerate)
                    cages = generator.generate(2, 5)
                }
                withContext(Dispatchers.IO) {
                    val sudokuParser = SudokuParser()
                    insertedBoardUid =
                        boardRepository.insert(
                            SudokuBoard(
                                uid = 0,
                                initialBoard = sudokuParser.boardToString(puzzle),
                                solvedBoard = sudokuParser.boardToString(solvedPuzzle),
                                difficulty = selectedDifficulty,
                                type = selectedType,
                                killerCages =
                                if (cages != null) {
                                    sudokuParser.killerSudokuCagesToString(
                                        cages
                                    )
                                } else {
                                    null
                                }
                            )
                        )
                }

                readyToPlay = true
            }
        }
    }

    fun changeDifficulty(diff: Int) {
        val indexToSet = difficulties.indexOf(selectedDifficulty) + diff
        if (indexToSet >= 0 && indexToSet < difficulties.count()) {
            selectedDifficulty = difficulties[indexToSet]
        }
    }

    fun changeType(diff: Int) {
        val indexToSet = types.indexOf(selectedType) + diff
        if (indexToSet >= 0 && indexToSet < types.count()) {
            selectedType = types[indexToSet]
        }
    }

    fun giveUpLastGame() {
        viewModelScope.launch(Dispatchers.IO) {
            lastSavedGame.value?.let {
                if (!it.completed) {
                    savedGameRepository.update(
                        it.copy(
                            completed = true,
                            canContinue = true
                        )
                    )
                }
            }
        }
    }

    fun claimReward() {
        viewModelScope.launch {
            when (val result = rewardCalendarManager.claimTodayReward()) {
                is ClaimResult.Success -> {
                    _claimedReward.value = result.reward
                }
                is ClaimResult.AlreadyClaimedToday,
                is ClaimResult.Error
                -> {
                    // Handle silently
                }
            }
        }
    }

    fun dismissClaimedReward() {
        _claimedReward.value = null
    }
}
