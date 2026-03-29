package sk.awisoft.sudokuplus.core.seasonal

import javax.inject.Inject
import javax.inject.Singleton
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.qqwing.QQWing
import sk.awisoft.sudokuplus.core.qqwing.QQWingController
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.utils.SudokuParser

@Singleton
class EventChallengeManager @Inject constructor() {

    private val difficulties = listOf(
        GameDifficulty.Easy,
        GameDifficulty.Moderate,
        GameDifficulty.Hard,
        GameDifficulty.Challenge
    )

    fun generatePuzzle(challenge: EventChallenge, seed: Long): PuzzleResult {
        val gameType = GameType.Default9x9

        val controller = QQWingController()
        val puzzle = controller.generateFromSeed(seed.toInt())

        val qqWing = QQWing(gameType, GameDifficulty.Unspecified)
        qqWing.setRecordHistory(true)
        qqWing.setPuzzle(puzzle)
        qqWing.solve()

        val parser = SudokuParser()
        return PuzzleResult(
            gameType = gameType,
            initialBoard = parser.boardToString(puzzle),
            solvedBoard = parser.boardToString(qqWing.solution),
            difficulty = qqWing.getDifficulty()
        )
    }

    fun getDefaultChallenges(durationDays: Int, eventType: EventType): List<EventChallenge> =
        (1..durationDays).map { day ->
            EventChallenge(
                day = day,
                difficulty = difficulties[(day - 1) % difficulties.size],
                xpMultiplier = EVENT_XP_MULTIPLIER
            )
        }

    data class PuzzleResult(
        val gameType: GameType,
        val initialBoard: String,
        val solvedBoard: String,
        val difficulty: GameDifficulty
    )

    companion object {
        const val EVENT_XP_MULTIPLIER = 2.0
    }
}
