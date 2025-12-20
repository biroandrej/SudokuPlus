package sk.awisoft.sudokuplus.ui.reward

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.reward.BadgeDefinition
import sk.awisoft.sudokuplus.core.reward.BadgeDefinitions
import sk.awisoft.sudokuplus.core.reward.ClaimResult
import sk.awisoft.sudokuplus.core.reward.DailyReward
import sk.awisoft.sudokuplus.core.reward.RewardCalendarManager
import sk.awisoft.sudokuplus.core.reward.RewardCalendarState
import sk.awisoft.sudokuplus.core.reward.RewardDefinitions
import sk.awisoft.sudokuplus.data.database.model.RewardBadge
import javax.inject.Inject

@HiltViewModel
class RewardCalendarViewModel @Inject constructor(
    private val rewardCalendarManager: RewardCalendarManager
) : ViewModel() {

    sealed interface UiEvent {
        data class RewardClaimed(val reward: DailyReward, val earnedBadge: BadgeDefinition? = null) : UiEvent
        data object AlreadyClaimed : UiEvent
    }

    private val _uiEvents = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvents = _uiEvents.asSharedFlow()

    val calendarState: StateFlow<RewardCalendarState?> = rewardCalendarManager.getCalendarState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rewardCycle: List<DailyReward> = RewardDefinitions.rewardCycle

    val earnedBadges: StateFlow<List<RewardBadge>> = rewardCalendarManager.getEarnedBadges()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badgeDefinitions: List<BadgeDefinition> = BadgeDefinitions.all

    private val _isClaimingReward = MutableStateFlow(false)
    val isClaimingReward: StateFlow<Boolean> = _isClaimingReward.asStateFlow()

    fun claimReward() {
        if (_isClaimingReward.value) return

        viewModelScope.launch {
            _isClaimingReward.value = true
            when (val result = rewardCalendarManager.claimTodayReward()) {
                is ClaimResult.Success -> {
                    _uiEvents.emit(UiEvent.RewardClaimed(result.reward, result.earnedBadge))
                }
                is ClaimResult.AlreadyClaimedToday -> {
                    _uiEvents.emit(UiEvent.AlreadyClaimed)
                }
                is ClaimResult.Error -> {
                    // Handle error silently
                }
            }
            _isClaimingReward.value = false
        }
    }
}
