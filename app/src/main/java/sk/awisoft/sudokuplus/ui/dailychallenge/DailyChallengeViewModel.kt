package sk.awisoft.sudokuplus.ui.dailychallenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val manager: DailyChallengeManager,
    private val repository: DailyChallengeRepository
) : ViewModel() {

    private val _todayChallenge = MutableStateFlow<DailyChallenge?>(null)
    val todayChallenge: StateFlow<DailyChallenge?> = _todayChallenge.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val completedCount: StateFlow<Int> = repository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak: StateFlow<Int> = repository.getCompleted()
        .map { challenges ->
            manager.calculateCurrentStreak(challenges.map { it.date })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestStreak: StateFlow<Int> = repository.getCompleted()
        .map { challenges ->
            manager.calculateBestStreak(challenges.map { it.date })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadTodayChallenge()
    }

    fun loadTodayChallenge() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _todayChallenge.value = manager.getOrCreateTodayChallenge()
            _isLoading.value = false
        }
    }

    fun isTodayCompleted(): Boolean {
        return _todayChallenge.value?.completedAt != null
    }

    fun isTodayInProgress(): Boolean {
        val challenge = _todayChallenge.value ?: return false
        return challenge.currentBoard != null && challenge.completedAt == null
    }
}

@HiltViewModel
class DailyChallengeCalendarViewModel @Inject constructor(
    private val manager: DailyChallengeManager,
    private val repository: DailyChallengeRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _challengesInMonth = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val challengesInMonth: StateFlow<List<DailyChallenge>> = _challengesInMonth.asStateFlow()

    private val _selectedChallenge = MutableStateFlow<DailyChallenge?>(null)
    val selectedChallenge: StateFlow<DailyChallenge?> = _selectedChallenge.asStateFlow()

    val completedCount: StateFlow<Int> = repository.getCompletedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val currentStreak: StateFlow<Int> = repository.getCompleted()
        .map { challenges ->
            manager.calculateCurrentStreak(challenges.map { it.date })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val bestStreak: StateFlow<Int> = repository.getCompleted()
        .map { challenges ->
            manager.calculateBestStreak(challenges.map { it.date })
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadChallengesForMonth()
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
        loadChallengesForMonth()
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
        loadChallengesForMonth()
    }

    fun selectDay(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedChallenge.value = repository.get(date)
        }
    }

    fun clearSelection() {
        _selectedChallenge.value = null
    }

    private fun loadChallengesForMonth() {
        viewModelScope.launch(Dispatchers.IO) {
            val month = _currentMonth.value
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()

            repository.getInRange(startDate, endDate).collect { challenges ->
                _challengesInMonth.value = challenges
            }
        }
    }
}
