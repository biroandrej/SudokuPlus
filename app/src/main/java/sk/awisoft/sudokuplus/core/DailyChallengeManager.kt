package sk.awisoft.sudokuplus.core

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.qqwing.QQWing
import sk.awisoft.sudokuplus.core.qqwing.QQWingController
import sk.awisoft.sudokuplus.core.utils.SudokuParser
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository

@Singleton
class DailyChallengeManager
@Inject
constructor(
    private val repository: DailyChallengeRepository
) {
    private val difficulties =
        listOf(
            GameDifficulty.Easy,
            GameDifficulty.Moderate,
            GameDifficulty.Hard,
            GameDifficulty.Challenge
        )

    fun getDifficultyForDate(date: LocalDate): GameDifficulty {
        val dayIndex = date.toEpochDay().toInt()
        return difficulties[Math.abs(dayIndex) % difficulties.size]
    }

    fun getSeedForDate(date: LocalDate): Long {
        return date.toEpochDay() * 1_000_000L + 42
    }

    suspend fun getOrCreateTodayChallenge(): DailyChallenge {
        return getOrCreateChallenge(LocalDate.now())
    }

    suspend fun getOrCreateChallenge(date: LocalDate): DailyChallenge {
        repository.get(date)?.let { return it }

        return withContext(Dispatchers.Default) {
            val seed = getSeedForDate(date)
            val gameType = GameType.Default9x9

            val controller = QQWingController()
            val puzzle = controller.generateFromSeed(seed.toInt())

            // Use QQWing directly to solve and determine difficulty
            val qqWing = QQWing(gameType, GameDifficulty.Unspecified)
            qqWing.setRecordHistory(true)
            qqWing.setPuzzle(puzzle)
            qqWing.solve()

            val solved = qqWing.solution
            val difficulty = qqWing.getDifficulty()

            val parser = SudokuParser()
            val challenge =
                DailyChallenge(
                    date = date,
                    difficulty = difficulty,
                    gameType = gameType,
                    seed = seed,
                    initialBoard = parser.boardToString(puzzle),
                    solvedBoard = parser.boardToString(solved)
                )

            repository.save(challenge)
            challenge
        }
    }

    fun calculateCurrentStreak(completedDates: List<LocalDate>): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.sortedDescending()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        if (sortedDates.first() != today && sortedDates.first() != yesterday) {
            return 0
        }

        var streak = 1
        var currentDate = sortedDates.first()

        for (i in 1 until sortedDates.size) {
            val expectedPrevious = currentDate.minusDays(1)
            if (sortedDates[i] == expectedPrevious) {
                streak++
                currentDate = sortedDates[i]
            } else {
                break
            }
        }

        return streak
    }

    fun calculateBestStreak(completedDates: List<LocalDate>): Int {
        if (completedDates.isEmpty()) return 0

        val sortedDates = completedDates.sorted()
        var bestStreak = 1
        var currentStreak = 1

        for (i in 1 until sortedDates.size) {
            if (sortedDates[i] == sortedDates[i - 1].plusDays(1)) {
                currentStreak++
                if (currentStreak > bestStreak) {
                    bestStreak = currentStreak
                }
            } else if (sortedDates[i] != sortedDates[i - 1]) {
                currentStreak = 1
            }
        }

        return bestStreak
    }
}
