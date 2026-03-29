package sk.awisoft.sudokuplus.ui.seasonal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.seasonal.SeasonalEventEngine
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.model.EventProgressEntity
import sk.awisoft.sudokuplus.domain.repository.SeasonalEventRepository

data class EventWithProgress(
    val event: SeasonalEvent,
    val progress: EventProgressEntity?
)

@HiltViewModel
class EventsViewModel @Inject constructor(
    private val repository: SeasonalEventRepository,
    private val eventEngine: SeasonalEventEngine
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun selectEvent(event: SeasonalEvent) {
        viewModelScope.launch {
            val progress = repository.getEventProgress(event.id)
            _selectedEvent.value = EventWithProgress(event, progress)
        }
    }

    fun getCurrentDay(event: SeasonalEvent): Int = eventEngine.getCurrentEventDay(event)

    fun getCompletionPercentage(event: SeasonalEvent, challengesCompleted: Int): Int =
        eventEngine.getCompletionPercentage(event, challengesCompleted)

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
