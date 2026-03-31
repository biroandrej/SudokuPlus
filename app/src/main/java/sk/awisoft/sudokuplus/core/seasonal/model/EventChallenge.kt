package sk.awisoft.sudokuplus.core.seasonal.model

import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty

data class EventChallenge(
    val day: Int,
    val difficulty: GameDifficulty,
    val xpMultiplier: Double = 1.5
)
