package sk.awisoft.sudokuplus.data.database.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType

enum class AchievementCategory(@StringRes val displayName: Int) {
    COMPLETION(R.string.achievement_category_completion),
    SPEED(R.string.achievement_category_speed),
    ACCURACY(R.string.achievement_category_accuracy),
    STREAK(R.string.achievement_category_streak),
    VARIETY(R.string.achievement_category_variety),
    MASTERY(R.string.achievement_category_mastery),
    DAILY(R.string.achievement_category_daily)
}

sealed class AchievementRequirement {
    data class GamesCompleted(val count: Int) : AchievementRequirement()
    data class GamesCompletedWithDifficulty(val count: Int, val difficulty: GameDifficulty) : AchievementRequirement()
    data class GamesCompletedWithType(val count: Int, val gameType: GameType) : AchievementRequirement()
    data class GamesCompletedNoMistakes(val count: Int) : AchievementRequirement()
    data class GamesCompletedNoHints(val count: Int) : AchievementRequirement()
    data class GamesCompletedUnderTime(val seconds: Int, val difficulty: GameDifficulty) : AchievementRequirement()
    data class DailyStreak(val days: Int) : AchievementRequirement()
    data class DailyChallengesCompleted(val count: Int) : AchievementRequirement()
    data class TotalPlayTime(val minutes: Int) : AchievementRequirement()
    data class PlayStreak(val days: Int) : AchievementRequirement()
}

data class AchievementDefinition(
    val id: String,
    val category: AchievementCategory,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
    val requirement: AchievementRequirement,
    val tier: AchievementTier = AchievementTier.BRONZE
)

enum class AchievementTier(@StringRes val displayName: Int) {
    BRONZE(R.string.achievement_tier_bronze),
    SILVER(R.string.achievement_tier_silver),
    GOLD(R.string.achievement_tier_gold),
    PLATINUM(R.string.achievement_tier_platinum)
}
