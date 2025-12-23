package sk.awisoft.sudokuplus.core.xp

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.core.achievement.GameCompletionData
import sk.awisoft.sudokuplus.core.reward.RewardCalendarManager
import sk.awisoft.sudokuplus.data.database.model.UserProgress
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.UserProgressRepository

data class XPResult(
    val baseXP: Int,
    val bonuses: List<XPBonus>,
    val totalXP: Long,
    val previousLevel: Int,
    val newLevel: Int,
    val leveledUp: Boolean,
    val updatedProgress: UserProgress
)

data class XPBonus(
    val type: XPBonusType,
    val multiplier: Float,
    val streakDays: Int = 0
)

enum class XPBonusType {
    NO_MISTAKES,
    NO_HINTS,
    DAILY_CHALLENGE,
    STREAK,
    REWARD_BOOST
}

@Singleton
class XPEngine
@Inject
constructor(
    private val userProgressRepository: UserProgressRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val dailyChallengeManager: DailyChallengeManager,
    private val rewardCalendarManager: RewardCalendarManager
) {
    /**
     * Award XP for completing a game
     * Returns XPResult with breakdown of XP earned and level changes
     */
    suspend fun awardXP(completionData: GameCompletionData): XPResult {
        val previousProgress = userProgressRepository.getSync() ?: UserProgress()
        val previousLevel = previousProgress.level

        // Calculate base XP
        val baseXP = XPConfig.getBaseXP(completionData.difficulty)

        // Calculate bonuses
        val bonuses = mutableListOf<XPBonus>()
        var multiplier = 1.0f

        // No mistakes bonus
        if (completionData.mistakes == 0) {
            bonuses.add(
                XPBonus(
                    type = XPBonusType.NO_MISTAKES,
                    multiplier = XPConfig.NO_MISTAKES_BONUS
                )
            )
            multiplier *= XPConfig.NO_MISTAKES_BONUS
        }

        // No hints bonus
        if (completionData.hintsUsed == 0) {
            bonuses.add(
                XPBonus(
                    type = XPBonusType.NO_HINTS,
                    multiplier = XPConfig.NO_HINTS_BONUS
                )
            )
            multiplier *= XPConfig.NO_HINTS_BONUS
        }

        // Daily challenge bonus
        if (completionData.isDailyChallenge) {
            bonuses.add(
                XPBonus(
                    type = XPBonusType.DAILY_CHALLENGE,
                    multiplier = XPConfig.DAILY_CHALLENGE_BONUS
                )
            )
            multiplier *= XPConfig.DAILY_CHALLENGE_BONUS
        }

        // Streak bonus
        val currentStreak = getCurrentStreak()
        if (currentStreak > 0) {
            val streakMultiplier =
                1f +
                    minOf(
                        currentStreak * XPConfig.STREAK_BONUS_PER_DAY,
                        XPConfig.MAX_STREAK_BONUS
                    )
            bonuses.add(
                XPBonus(
                    type = XPBonusType.STREAK,
                    multiplier = streakMultiplier,
                    streakDays = currentStreak
                )
            )
            multiplier *= streakMultiplier
        }

        // Reward calendar XP boost (2x XP)
        if (rewardCalendarManager.hasXPBoost()) {
            bonuses.add(
                XPBonus(
                    type = XPBonusType.REWARD_BOOST,
                    multiplier = 2.0f
                )
            )
            multiplier *= 2.0f
            // Consume the boost
            rewardCalendarManager.useXPBoost()
        }

        // Calculate total XP
        val totalXP = (baseXP * multiplier).toLong()

        // Award XP and get updated progress
        val updatedProgress = userProgressRepository.addXP(totalXP)
        val leveledUp = updatedProgress.level > previousLevel

        return XPResult(
            baseXP = baseXP,
            bonuses = bonuses,
            totalXP = totalXP,
            previousLevel = previousLevel,
            newLevel = updatedProgress.level,
            leveledUp = leveledUp,
            updatedProgress = updatedProgress
        )
    }

    private suspend fun getCurrentStreak(): Int {
        val completed = dailyChallengeRepository.getCompleted().first()
        return dailyChallengeManager.calculateCurrentStreak(completed.map { it.date })
    }
}
