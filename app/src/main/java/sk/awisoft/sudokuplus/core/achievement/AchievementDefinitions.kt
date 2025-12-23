package sk.awisoft.sudokuplus.core.achievement

import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.database.model.AchievementCategory
import sk.awisoft.sudokuplus.data.database.model.AchievementDefinition
import sk.awisoft.sudokuplus.data.database.model.AchievementRequirement
import sk.awisoft.sudokuplus.data.database.model.AchievementTier

object AchievementDefinitions {
    val all: List<AchievementDefinition> =
        listOf(
            // ===== COMPLETION ACHIEVEMENTS =====
            AchievementDefinition(
                id = "first_win",
                category = AchievementCategory.COMPLETION,
                nameRes = R.string.achievement_first_win,
                descriptionRes = R.string.achievement_first_win_desc,
                iconRes = R.drawable.ic_achievement_first_win,
                requirement = AchievementRequirement.GamesCompleted(1),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "ten_games",
                category = AchievementCategory.COMPLETION,
                nameRes = R.string.achievement_ten_games,
                descriptionRes = R.string.achievement_ten_games_desc,
                iconRes = R.drawable.ic_achievement_ten_games,
                requirement = AchievementRequirement.GamesCompleted(10),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "fifty_games",
                category = AchievementCategory.COMPLETION,
                nameRes = R.string.achievement_fifty_games,
                descriptionRes = R.string.achievement_fifty_games_desc,
                iconRes = R.drawable.ic_achievement_fifty_games,
                requirement = AchievementRequirement.GamesCompleted(50),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "centurion",
                category = AchievementCategory.COMPLETION,
                nameRes = R.string.achievement_centurion,
                descriptionRes = R.string.achievement_centurion_desc,
                iconRes = R.drawable.ic_achievement_centurion,
                requirement = AchievementRequirement.GamesCompleted(100),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "dedicated_player",
                category = AchievementCategory.COMPLETION,
                nameRes = R.string.achievement_dedicated_player,
                descriptionRes = R.string.achievement_dedicated_player_desc,
                iconRes = R.drawable.ic_achievement_dedicated,
                requirement = AchievementRequirement.GamesCompleted(500),
                tier = AchievementTier.PLATINUM
            ),
            // ===== SPEED ACHIEVEMENTS =====
            AchievementDefinition(
                id = "speed_easy_5",
                category = AchievementCategory.SPEED,
                nameRes = R.string.achievement_speed_easy_5,
                descriptionRes = R.string.achievement_speed_easy_5_desc,
                iconRes = R.drawable.ic_achievement_speed,
                requirement = AchievementRequirement.GamesCompletedUnderTime(
                    300,
                    GameDifficulty.Easy
                ),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "speed_easy_3",
                category = AchievementCategory.SPEED,
                nameRes = R.string.achievement_speed_easy_3,
                descriptionRes = R.string.achievement_speed_easy_3_desc,
                iconRes = R.drawable.ic_achievement_speed,
                requirement = AchievementRequirement.GamesCompletedUnderTime(
                    180,
                    GameDifficulty.Easy
                ),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "speed_moderate_10",
                category = AchievementCategory.SPEED,
                nameRes = R.string.achievement_speed_moderate_10,
                descriptionRes = R.string.achievement_speed_moderate_10_desc,
                iconRes = R.drawable.ic_achievement_speed,
                requirement = AchievementRequirement.GamesCompletedUnderTime(
                    600,
                    GameDifficulty.Moderate
                ),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "speed_hard_15",
                category = AchievementCategory.SPEED,
                nameRes = R.string.achievement_speed_hard_15,
                descriptionRes = R.string.achievement_speed_hard_15_desc,
                iconRes = R.drawable.ic_achievement_speed,
                requirement = AchievementRequirement.GamesCompletedUnderTime(
                    900,
                    GameDifficulty.Hard
                ),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "speed_challenge_20",
                category = AchievementCategory.SPEED,
                nameRes = R.string.achievement_speed_challenge_20,
                descriptionRes = R.string.achievement_speed_challenge_20_desc,
                iconRes = R.drawable.ic_achievement_speed,
                requirement = AchievementRequirement.GamesCompletedUnderTime(
                    1200,
                    GameDifficulty.Challenge
                ),
                tier = AchievementTier.PLATINUM
            ),
            // ===== ACCURACY ACHIEVEMENTS =====
            AchievementDefinition(
                id = "perfectionist_1",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_perfectionist_1,
                descriptionRes = R.string.achievement_perfectionist_1_desc,
                iconRes = R.drawable.ic_achievement_perfect,
                requirement = AchievementRequirement.GamesCompletedNoMistakes(1),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "perfectionist_10",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_perfectionist_10,
                descriptionRes = R.string.achievement_perfectionist_10_desc,
                iconRes = R.drawable.ic_achievement_perfect,
                requirement = AchievementRequirement.GamesCompletedNoMistakes(10),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "perfectionist_50",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_perfectionist_50,
                descriptionRes = R.string.achievement_perfectionist_50_desc,
                iconRes = R.drawable.ic_achievement_perfect,
                requirement = AchievementRequirement.GamesCompletedNoMistakes(50),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "no_hints_1",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_no_hints_1,
                descriptionRes = R.string.achievement_no_hints_1_desc,
                iconRes = R.drawable.ic_achievement_no_hints,
                requirement = AchievementRequirement.GamesCompletedNoHints(1),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "no_hints_25",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_no_hints_25,
                descriptionRes = R.string.achievement_no_hints_25_desc,
                iconRes = R.drawable.ic_achievement_no_hints,
                requirement = AchievementRequirement.GamesCompletedNoHints(25),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "no_hints_100",
                category = AchievementCategory.ACCURACY,
                nameRes = R.string.achievement_no_hints_100,
                descriptionRes = R.string.achievement_no_hints_100_desc,
                iconRes = R.drawable.ic_achievement_no_hints,
                requirement = AchievementRequirement.GamesCompletedNoHints(100),
                tier = AchievementTier.GOLD
            ),
            // ===== STREAK ACHIEVEMENTS =====
            AchievementDefinition(
                id = "streak_3",
                category = AchievementCategory.STREAK,
                nameRes = R.string.achievement_streak_3,
                descriptionRes = R.string.achievement_streak_3_desc,
                iconRes = R.drawable.ic_achievement_streak,
                requirement = AchievementRequirement.PlayStreak(3),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "streak_7",
                category = AchievementCategory.STREAK,
                nameRes = R.string.achievement_streak_7,
                descriptionRes = R.string.achievement_streak_7_desc,
                iconRes = R.drawable.ic_achievement_streak,
                requirement = AchievementRequirement.PlayStreak(7),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "streak_30",
                category = AchievementCategory.STREAK,
                nameRes = R.string.achievement_streak_30,
                descriptionRes = R.string.achievement_streak_30_desc,
                iconRes = R.drawable.ic_achievement_streak,
                requirement = AchievementRequirement.PlayStreak(30),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "streak_100",
                category = AchievementCategory.STREAK,
                nameRes = R.string.achievement_streak_100,
                descriptionRes = R.string.achievement_streak_100_desc,
                iconRes = R.drawable.ic_achievement_streak,
                requirement = AchievementRequirement.PlayStreak(100),
                tier = AchievementTier.PLATINUM
            ),
            // ===== VARIETY ACHIEVEMENTS =====
            AchievementDefinition(
                id = "try_6x6",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_try_6x6,
                descriptionRes = R.string.achievement_try_6x6_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(1, GameType.Default6x6),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "try_12x12",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_try_12x12,
                descriptionRes = R.string.achievement_try_12x12_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(
                    1,
                    GameType.Default12x12
                ),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "try_killer",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_try_killer,
                descriptionRes = R.string.achievement_try_killer_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(1, GameType.Killer9x9),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "killer_10",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_killer_10,
                descriptionRes = R.string.achievement_killer_10_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(10, GameType.Killer9x9),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "killer_50",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_killer_50,
                descriptionRes = R.string.achievement_killer_50_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(50, GameType.Killer9x9),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "giant_10",
                category = AchievementCategory.VARIETY,
                nameRes = R.string.achievement_giant_10,
                descriptionRes = R.string.achievement_giant_10_desc,
                iconRes = R.drawable.ic_achievement_variety,
                requirement = AchievementRequirement.GamesCompletedWithType(
                    10,
                    GameType.Default12x12
                ),
                tier = AchievementTier.SILVER
            ),
            // ===== MASTERY ACHIEVEMENTS =====
            AchievementDefinition(
                id = "easy_master",
                category = AchievementCategory.MASTERY,
                nameRes = R.string.achievement_easy_master,
                descriptionRes = R.string.achievement_easy_master_desc,
                iconRes = R.drawable.ic_achievement_mastery,
                requirement = AchievementRequirement.GamesCompletedWithDifficulty(
                    25,
                    GameDifficulty.Easy
                ),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "moderate_master",
                category = AchievementCategory.MASTERY,
                nameRes = R.string.achievement_moderate_master,
                descriptionRes = R.string.achievement_moderate_master_desc,
                iconRes = R.drawable.ic_achievement_mastery,
                requirement = AchievementRequirement.GamesCompletedWithDifficulty(
                    25,
                    GameDifficulty.Moderate
                ),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "hard_master",
                category = AchievementCategory.MASTERY,
                nameRes = R.string.achievement_hard_master,
                descriptionRes = R.string.achievement_hard_master_desc,
                iconRes = R.drawable.ic_achievement_mastery,
                requirement = AchievementRequirement.GamesCompletedWithDifficulty(
                    25,
                    GameDifficulty.Hard
                ),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "challenge_master",
                category = AchievementCategory.MASTERY,
                nameRes = R.string.achievement_challenge_master,
                descriptionRes = R.string.achievement_challenge_master_desc,
                iconRes = R.drawable.ic_achievement_mastery,
                requirement = AchievementRequirement.GamesCompletedWithDifficulty(
                    25,
                    GameDifficulty.Challenge
                ),
                tier = AchievementTier.PLATINUM
            ),
            // ===== DAILY CHALLENGE ACHIEVEMENTS =====
            AchievementDefinition(
                id = "daily_first",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_first,
                descriptionRes = R.string.achievement_daily_first_desc,
                iconRes = R.drawable.ic_achievement_daily,
                requirement = AchievementRequirement.DailyChallengesCompleted(1),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "daily_week",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_week,
                descriptionRes = R.string.achievement_daily_week_desc,
                iconRes = R.drawable.ic_achievement_daily,
                requirement = AchievementRequirement.DailyChallengesCompleted(7),
                tier = AchievementTier.BRONZE
            ),
            AchievementDefinition(
                id = "daily_month",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_month,
                descriptionRes = R.string.achievement_daily_month_desc,
                iconRes = R.drawable.ic_achievement_daily,
                requirement = AchievementRequirement.DailyChallengesCompleted(30),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "daily_streak_7",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_streak_7,
                descriptionRes = R.string.achievement_daily_streak_7_desc,
                iconRes = R.drawable.ic_achievement_daily_streak,
                requirement = AchievementRequirement.DailyStreak(7),
                tier = AchievementTier.SILVER
            ),
            AchievementDefinition(
                id = "daily_streak_30",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_streak_30,
                descriptionRes = R.string.achievement_daily_streak_30_desc,
                iconRes = R.drawable.ic_achievement_daily_streak,
                requirement = AchievementRequirement.DailyStreak(30),
                tier = AchievementTier.GOLD
            ),
            AchievementDefinition(
                id = "daily_streak_100",
                category = AchievementCategory.DAILY,
                nameRes = R.string.achievement_daily_streak_100,
                descriptionRes = R.string.achievement_daily_streak_100_desc,
                iconRes = R.drawable.ic_achievement_daily_streak,
                requirement = AchievementRequirement.DailyStreak(100),
                tier = AchievementTier.PLATINUM
            )
        )

    fun getById(id: String): AchievementDefinition? = all.find { it.id == id }

    fun getRequirementValue(requirement: AchievementRequirement): Int = when (requirement) {
        is AchievementRequirement.GamesCompleted -> requirement.count
        is AchievementRequirement.GamesCompletedWithDifficulty -> requirement.count
        is AchievementRequirement.GamesCompletedWithType -> requirement.count
        is AchievementRequirement.GamesCompletedNoMistakes -> requirement.count
        is AchievementRequirement.GamesCompletedNoHints -> requirement.count
        is AchievementRequirement.GamesCompletedUnderTime -> 1
        is AchievementRequirement.DailyStreak -> requirement.days
        is AchievementRequirement.DailyChallengesCompleted -> requirement.count
        is AchievementRequirement.TotalPlayTime -> requirement.minutes
        is AchievementRequirement.PlayStreak -> requirement.days
    }
}
