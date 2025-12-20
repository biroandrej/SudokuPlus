package sk.awisoft.sudokuplus.core.reward

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import sk.awisoft.sudokuplus.data.database.dao.RewardBadgeDao
import sk.awisoft.sudokuplus.data.database.model.ClaimedReward
import sk.awisoft.sudokuplus.data.database.model.LoginRewardStatus
import sk.awisoft.sudokuplus.data.database.model.RewardBadge
import sk.awisoft.sudokuplus.domain.repository.LoginRewardRepository
import java.time.LocalDate
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton

sealed class ClaimResult {
    data class Success(
        val reward: DailyReward,
        val newStatus: LoginRewardStatus,
        val earnedBadge: BadgeDefinition? = null
    ) : ClaimResult()

    data object AlreadyClaimedToday : ClaimResult()
    data object Error : ClaimResult()
}

data class RewardCalendarState(
    val currentDay: Int,
    val canClaimToday: Boolean,
    val todayReward: DailyReward,
    val bonusHints: Int,
    val xpBoostGamesRemaining: Int,
    val totalDaysClaimed: Int,
    val cycleProgress: Float
)

@Singleton
class RewardCalendarManager @Inject constructor(
    private val repository: LoginRewardRepository,
    private val badgeDao: RewardBadgeDao
) {

    fun getCalendarState(): Flow<RewardCalendarState> = repository.getStatus().map { status ->
        val currentStatus = status ?: LoginRewardStatus()
        val today = LocalDate.now()
        val canClaim = currentStatus.lastClaimDate != today

        RewardCalendarState(
            currentDay = currentStatus.currentDay,
            canClaimToday = canClaim,
            todayReward = RewardDefinitions.getRewardForDay(currentStatus.currentDay),
            bonusHints = currentStatus.bonusHints,
            xpBoostGamesRemaining = currentStatus.xpBoostGamesRemaining,
            totalDaysClaimed = currentStatus.totalDaysClaimed,
            cycleProgress = currentStatus.currentDay.toFloat() / RewardDefinitions.CYCLE_LENGTH
        )
    }

    suspend fun claimTodayReward(): ClaimResult {
        val currentStatus = repository.getStatusSync() ?: LoginRewardStatus()
        val today = LocalDate.now()

        // Check if already claimed today
        if (currentStatus.lastClaimDate == today) {
            return ClaimResult.AlreadyClaimedToday
        }

        val reward = RewardDefinitions.getRewardForDay(currentStatus.currentDay)

        // Calculate new status based on reward type
        val newStatus = when (reward.rewardType) {
            RewardType.HINTS -> currentStatus.copy(
                currentDay = getNextDay(currentStatus.currentDay),
                lastClaimDate = today,
                totalDaysClaimed = currentStatus.totalDaysClaimed + 1,
                bonusHints = currentStatus.bonusHints + reward.amount
            )
            RewardType.XP_BOOST -> currentStatus.copy(
                currentDay = getNextDay(currentStatus.currentDay),
                lastClaimDate = today,
                totalDaysClaimed = currentStatus.totalDaysClaimed + 1,
                xpBoostGamesRemaining = currentStatus.xpBoostGamesRemaining + reward.amount
            )
            RewardType.BADGE -> currentStatus.copy(
                currentDay = getNextDay(currentStatus.currentDay),
                lastClaimDate = today,
                totalDaysClaimed = currentStatus.totalDaysClaimed + 1
            )
        }

        repository.updateStatus(newStatus)

        // Record the claimed reward
        repository.recordClaimedReward(
            ClaimedReward(
                day = currentStatus.currentDay,
                rewardType = reward.rewardType,
                amount = reward.amount,
                claimedAt = ZonedDateTime.now()
            )
        )

        // Award badge if this is a badge reward
        var earnedBadge: BadgeDefinition? = null
        if (reward.rewardType == RewardType.BADGE) {
            earnedBadge = BadgeDefinitions.getByDay(currentStatus.currentDay)
            if (earnedBadge != null) {
                val cycleNumber = (currentStatus.totalDaysClaimed / 30) + 1
                badgeDao.insert(
                    RewardBadge(
                        badgeId = earnedBadge.id,
                        earnedAt = ZonedDateTime.now(),
                        cycleNumber = cycleNumber
                    )
                )
            }
        }

        return ClaimResult.Success(reward, newStatus, earnedBadge)
    }

    suspend fun useHint(): Boolean {
        val currentStatus = repository.getStatusSync() ?: return false
        if (currentStatus.bonusHints <= 0) return false

        repository.updateStatus(
            currentStatus.copy(bonusHints = currentStatus.bonusHints - 1)
        )
        return true
    }

    suspend fun useXPBoost(): Boolean {
        val currentStatus = repository.getStatusSync() ?: return false
        if (currentStatus.xpBoostGamesRemaining <= 0) return false

        repository.updateStatus(
            currentStatus.copy(xpBoostGamesRemaining = currentStatus.xpBoostGamesRemaining - 1)
        )
        return true
    }

    suspend fun hasXPBoost(): Boolean {
        val currentStatus = repository.getStatusSync() ?: return false
        return currentStatus.xpBoostGamesRemaining > 0
    }

    suspend fun getBonusHints(): Int {
        return repository.getStatusSync()?.bonusHints ?: 0
    }

    fun getEarnedBadges(): Flow<List<RewardBadge>> = badgeDao.getAll()

    private fun getNextDay(currentDay: Int): Int {
        return if (currentDay >= RewardDefinitions.CYCLE_LENGTH) 1 else currentDay + 1
    }
}
