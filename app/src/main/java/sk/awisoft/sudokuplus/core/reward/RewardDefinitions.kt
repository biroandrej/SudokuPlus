package sk.awisoft.sudokuplus.core.reward

import androidx.annotation.StringRes
import sk.awisoft.sudokuplus.R

enum class RewardType(@param: StringRes val displayName: Int) {
    HINTS(R.string.reward_type_hints),
    XP_BOOST(R.string.reward_type_xp_boost),
    BADGE(R.string.reward_type_badge)
}

data class DailyReward(
    val day: Int,
    val rewardType: RewardType,
    val amount: Int,
    val isSpecial: Boolean = false
)

/**
 * 30-day reward cycle with escalating rewards
 * Every 7th day is a special reward (badge)
 */
object RewardDefinitions {
    val rewardCycle: List<DailyReward> = listOf(
        // Week 1
        DailyReward(1, RewardType.HINTS, 1),
        DailyReward(2, RewardType.XP_BOOST, 1),
        DailyReward(3, RewardType.HINTS, 1),
        DailyReward(4, RewardType.XP_BOOST, 1),
        DailyReward(5, RewardType.HINTS, 2),
        DailyReward(6, RewardType.XP_BOOST, 2),
        DailyReward(7, RewardType.BADGE, 1, isSpecial = true),

        // Week 2
        DailyReward(8, RewardType.HINTS, 2),
        DailyReward(9, RewardType.XP_BOOST, 2),
        DailyReward(10, RewardType.HINTS, 2),
        DailyReward(11, RewardType.XP_BOOST, 2),
        DailyReward(12, RewardType.HINTS, 3),
        DailyReward(13, RewardType.XP_BOOST, 3),
        DailyReward(14, RewardType.BADGE, 1, isSpecial = true),

        // Week 3
        DailyReward(15, RewardType.HINTS, 3),
        DailyReward(16, RewardType.XP_BOOST, 3),
        DailyReward(17, RewardType.HINTS, 3),
        DailyReward(18, RewardType.XP_BOOST, 3),
        DailyReward(19, RewardType.HINTS, 4),
        DailyReward(20, RewardType.XP_BOOST, 4),
        DailyReward(21, RewardType.BADGE, 1, isSpecial = true),

        // Week 4
        DailyReward(22, RewardType.HINTS, 4),
        DailyReward(23, RewardType.XP_BOOST, 4),
        DailyReward(24, RewardType.HINTS, 4),
        DailyReward(25, RewardType.XP_BOOST, 4),
        DailyReward(26, RewardType.HINTS, 5),
        DailyReward(27, RewardType.XP_BOOST, 5),
        DailyReward(28, RewardType.BADGE, 1, isSpecial = true),

        // Final days
        DailyReward(29, RewardType.HINTS, 5),
        DailyReward(30, RewardType.BADGE, 1, isSpecial = true)
    )

    fun getRewardForDay(day: Int): DailyReward {
        val normalizedDay = ((day - 1) % 30) + 1
        return rewardCycle.first { it.day == normalizedDay }
    }

    const val CYCLE_LENGTH = 30
}
