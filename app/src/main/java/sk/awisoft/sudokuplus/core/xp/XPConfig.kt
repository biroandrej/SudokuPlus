package sk.awisoft.sudokuplus.core.xp

import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import kotlin.math.pow

object XPConfig {
    // Base XP by difficulty
    private val baseXPMap = mapOf(
        GameDifficulty.Simple to 10,
        GameDifficulty.Easy to 20,
        GameDifficulty.Moderate to 40,
        GameDifficulty.Hard to 70,
        GameDifficulty.Challenge to 100,
        GameDifficulty.Custom to 30, // Average for custom puzzles
        GameDifficulty.Unspecified to 20
    )

    fun getBaseXP(difficulty: GameDifficulty): Int = baseXPMap[difficulty] ?: 20

    // Multipliers
    const val NO_MISTAKES_BONUS = 1.5f
    const val NO_HINTS_BONUS = 1.25f
    const val DAILY_CHALLENGE_BONUS = 2.0f
    const val STREAK_BONUS_PER_DAY = 0.1f  // +10% per streak day
    const val MAX_STREAK_BONUS = 1.0f       // Max +100%

    /**
     * XP needed to reach the next level from current level
     * Formula: 100 * level^1.5
     */
    fun xpForLevel(level: Int): Long = (100 * level.toDouble().pow(1.5)).toLong()
}
