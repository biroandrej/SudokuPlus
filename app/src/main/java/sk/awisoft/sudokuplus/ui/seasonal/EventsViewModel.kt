package sk.awisoft.sudokuplus.ui.seasonal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.seasonal.EventBadgeDefinitions
import sk.awisoft.sudokuplus.core.seasonal.EventChallengeManager
import sk.awisoft.sudokuplus.core.seasonal.SeasonalEventEngine
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.model.EventChallengeGame
import sk.awisoft.sudokuplus.data.database.model.EventProgressEntity
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.domain.repository.SeasonalEventRepository

data class EventWithProgress(
    val event: SeasonalEvent,
    val progress: EventProgressEntity?
)

data class ChallengeCompleteCelebration(
    val eventType: EventType,
    val eventTitle: String,
    val challengeDay: Int,
    val completedCount: Int,
    val totalChallenges: Int,
    @androidx.annotation.StringRes val milestoneResId: Int?
)

data class BadgeEarnedCelebration(
    @androidx.annotation.StringRes val badgeNameRes: Int,
    val eventTitle: String,
    val eventType: EventType
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: SeasonalEventRepository,
    private val eventEngine: SeasonalEventEngine,
    private val challengeManager: EventChallengeManager,
    private val boardRepository: BoardRepository
) : ViewModel() {

    val activeEvents: StateFlow<List<SeasonalEvent>> =
        repository.getActiveEvents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingEvents: StateFlow<List<SeasonalEvent>> =
        repository.getUpcomingEvents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEvents: StateFlow<List<SeasonalEvent>> =
        repository.getAllEvents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedEvent = MutableStateFlow<EventWithProgress?>(null)
    val selectedEvent: StateFlow<EventWithProgress?> = _selectedEvent.asStateFlow()

    private val _currentEventId = MutableStateFlow<String?>(null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val completedDays: StateFlow<Set<Int>> =
        _currentEventId
            .flatMapLatest { eventId ->
                if (eventId == null) return@flatMapLatest flowOf(emptySet())
                repository.getChallengeGamesFlow(eventId)
                    .map { games ->
                        games.filter { it.completed }
                            .map { it.challengeDay }
                            .toSet()
                    }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _celebration = MutableStateFlow<ChallengeCompleteCelebration?>(null)
    val celebration: StateFlow<ChallengeCompleteCelebration?> = _celebration.asStateFlow()

    private val _badgeCelebration = MutableStateFlow<BadgeEarnedCelebration?>(null)
    val badgeCelebration: StateFlow<BadgeEarnedCelebration?> = _badgeCelebration.asStateFlow()

    // Snapshot of completed days before navigating to a game
    private var knownCompletedDays: Set<Int>? = null

    fun dismissCelebration() {
        _celebration.value = null
    }

    fun dismissBadgeCelebration() {
        _badgeCelebration.value = null
    }

    fun checkForNewCompletions() {
        val known = knownCompletedDays ?: return
        val eventId = _currentEventId.value ?: return
        viewModelScope.launch {
            val event = repository.getEventById(eventId) ?: return@launch
            val games = repository.getChallengeGames(eventId)
            val currentCompleted = games.filter { it.completed }.map { it.challengeDay }.toSet()
            val newDays = currentCompleted - known

            if (newDays.isNotEmpty()) {
                // Update snapshot so we don't re-trigger
                knownCompletedDays = currentCompleted

                val newDay = newDays.first()
                val completedCount = currentCompleted.size
                val total = event.challenges.size
                val milestoneResId = getMilestoneResId(completedCount, total)
                _celebration.value = ChallengeCompleteCelebration(
                    eventType = event.eventType,
                    eventTitle = event.title,
                    challengeDay = newDay,
                    completedCount = completedCount,
                    totalChallenges = total,
                    milestoneResId = milestoneResId
                )

                // Trigger badge earned when all challenges completed
                if (completedCount == total) {
                    val badge = EventBadgeDefinitions.getByEventType(event.eventType)
                    if (badge != null) {
                        _badgeCelebration.value = BadgeEarnedCelebration(
                            badgeNameRes = badge.nameRes,
                            eventTitle = event.title,
                            eventType = event.eventType
                        )
                    }
                }
            }
        }
    }

    private fun getMilestoneResId(completed: Int, total: Int): Int? {
        if (total == 0) return null
        val percent = (completed * 100) / total
        val prevPercent = ((completed - 1) * 100) / total
        return when {
            completed == total -> R.string.seasonal_event_milestone_100
            percent >= 75 && prevPercent < 75 -> R.string.seasonal_event_milestone_75
            percent >= 50 && prevPercent < 50 -> R.string.seasonal_event_milestone_50
            percent >= 25 && prevPercent < 25 -> R.string.seasonal_event_milestone_25
            else -> null
        }
    }

    fun selectEvent(event: SeasonalEvent) {
        _currentEventId.value = event.id
        viewModelScope.launch {
            val progress = repository.getEventProgress(event.id)
            _selectedEvent.value = EventWithProgress(event, progress)
        }
    }

    fun loadEvent(eventId: String) {
        _currentEventId.value = eventId
        viewModelScope.launch {
            val event = repository.getEventById(eventId) ?: return@launch
            val progress = repository.getEventProgress(eventId)
            _selectedEvent.value = EventWithProgress(event, progress)
        }
    }

    fun getCurrentDay(event: SeasonalEvent): Int = eventEngine.getCurrentEventDay(event)

    fun getCompletionPercentage(event: SeasonalEvent, challengesCompleted: Int): Int =
        eventEngine.getCompletionPercentage(event, challengesCompleted)

    private val _gameReady = MutableStateFlow<Long?>(null)
    val gameReady: StateFlow<Long?> = _gameReady.asStateFlow()

    fun consumeGameReady() {
        _gameReady.value = null
    }

    fun playChallenge(event: SeasonalEvent, challenge: EventChallenge) {
        // Snapshot current completions before navigating to game
        knownCompletedDays = completedDays.value
        viewModelScope.launch {
            // Check if already started this challenge
            val existingGames = repository.getChallengeGames(event.id)
            val existing = existingGames.find { it.challengeDay == challenge.day }
            if (existing != null) {
                // Resume existing game
                _gameReady.value = existing.boardUid
                return@launch
            }

            _isLoading.value = true
            try {
                val seed = eventEngine.getEventSeed(event, challenge.day)
                val result = withContext(Dispatchers.Default) {
                    challengeManager.generatePuzzle(challenge, seed)
                }

                val boardUid = withContext(Dispatchers.IO) {
                    boardRepository.insert(
                        SudokuBoard(
                            uid = 0,
                            initialBoard = result.initialBoard,
                            solvedBoard = result.solvedBoard,
                            difficulty = result.difficulty,
                            type = result.gameType
                        )
                    )
                }

                // Record the challenge game link
                withContext(Dispatchers.IO) {
                    repository.insertChallengeGame(
                        EventChallengeGame(
                            eventId = event.id,
                            challengeDay = challenge.day,
                            boardUid = boardUid
                        )
                    )
                }

                _gameReady.value = boardUid
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.syncEvents()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
