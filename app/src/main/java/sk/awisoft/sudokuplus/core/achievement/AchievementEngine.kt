package sk.awisoft.sudokuplus.core.achievement

import java.time.Duration
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.database.model.AchievementDefinition
import sk.awisoft.sudokuplus.data.database.model.AchievementRequirement
import sk.awisoft.sudokuplus.domain.repository.AchievementRepository
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.RecordRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository

data class GameCompletionData(
    val difficulty: GameDifficulty,
    val gameType: GameType,
    val completionTime: Duration,
    val mistakes: Int,
    val hintsUsed: Int,
    val isDailyChallenge: Boolean = false
)

@Singleton
class AchievementEngine
@Inject
constructor(
    private val achievementRepository: AchievementRepository,
    private val savedGameRepository: SavedGameRepository,
    private val recordRepository: RecordRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val dailyChallengeManager: DailyChallengeManager
) {
    /**
     * Check and unlock achievements after a game completion
     * Returns list of newly unlocked achievements
     */
    suspend fun checkAchievements(completionData: GameCompletionData): List<AchievementDefinition> {
        val newlyUnlocked = mutableListOf<AchievementDefinition>()
        val currentAchievements = achievementRepository.getAll().first().associateBy { it.achievementId }

        for (definition in AchievementDefinitions.all) {
            val userAchievement = currentAchievements[definition.id]

            // Skip already unlocked achievements
            if (userAchievement?.isUnlocked == true) continue

            val (progress, isCompleted) = calculateProgress(definition, completionData)

            if (isCompleted) {
                achievementRepository.unlock(definition.id)
                newlyUnlocked.add(definition)
            } else if (progress > (userAchievement?.progress ?: 0)) {
                achievementRepository.updateProgress(definition.id, progress)
            }
        }

        return newlyUnlocked
    }

    /**
     * Check all achievements and update progress (for initialization or sync)
     */
    suspend fun recalculateAllProgress(): List<AchievementDefinition> {
        val newlyUnlocked = mutableListOf<AchievementDefinition>()
        val currentAchievements = achievementRepository.getAll().first().associateBy { it.achievementId }

        for (definition in AchievementDefinitions.all) {
            val userAchievement = currentAchievements[definition.id]

            // Skip already unlocked achievements
            if (userAchievement?.isUnlocked == true) continue

            val (progress, isCompleted) = calculateProgressFromHistory(definition)

            if (isCompleted) {
                achievementRepository.unlock(definition.id)
                newlyUnlocked.add(definition)
            } else {
                achievementRepository.updateProgress(definition.id, progress)
            }
        }

        return newlyUnlocked
    }

    private suspend fun calculateProgress(
        definition: AchievementDefinition,
        completionData: GameCompletionData
    ): Pair<Int, Boolean> {
        return when (val requirement = definition.requirement) {
            is AchievementRequirement.GamesCompleted -> {
                val count = getCompletedGamesCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedWithDifficulty -> {
                val count = getCompletedGamesCountByDifficulty(requirement.difficulty)
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedWithType -> {
                val count = getCompletedGamesCountByType(requirement.gameType)
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedNoMistakes -> {
                val count = getCompletedGamesNoMistakesCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedNoHints -> {
                val count = getCompletedGamesNoHintsCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedUnderTime -> {
                val isCompleted =
                    completionData.difficulty == requirement.difficulty &&
                        completionData.completionTime.seconds <= requirement.seconds
                val progress = if (isCompleted) 1 else 0
                progress to isCompleted
            }

            is AchievementRequirement.DailyStreak -> {
                val streak = getCurrentDailyStreak()
                streak to (streak >= requirement.days)
            }

            is AchievementRequirement.DailyChallengesCompleted -> {
                val count = getDailyChallengesCompletedCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.TotalPlayTime -> {
                val minutes = getTotalPlayTimeMinutes()
                minutes to (minutes >= requirement.minutes)
            }

            is AchievementRequirement.PlayStreak -> {
                val streak = getPlayStreak()
                streak to (streak >= requirement.days)
            }
        }
    }

    private suspend fun calculateProgressFromHistory(
        definition: AchievementDefinition
    ): Pair<Int, Boolean> {
        return when (val requirement = definition.requirement) {
            is AchievementRequirement.GamesCompleted -> {
                val count = getCompletedGamesCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedWithDifficulty -> {
                val count = getCompletedGamesCountByDifficulty(requirement.difficulty)
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedWithType -> {
                val count = getCompletedGamesCountByType(requirement.gameType)
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedNoMistakes -> {
                val count = getCompletedGamesNoMistakesCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedNoHints -> {
                val count = getCompletedGamesNoHintsCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.GamesCompletedUnderTime -> {
                val hasAny = hasCompletedUnderTime(requirement.difficulty, requirement.seconds)
                val progress = if (hasAny) 1 else 0
                progress to hasAny
            }

            is AchievementRequirement.DailyStreak -> {
                val streak = getBestDailyStreak()
                streak to (streak >= requirement.days)
            }

            is AchievementRequirement.DailyChallengesCompleted -> {
                val count = getDailyChallengesCompletedCount()
                count to (count >= requirement.count)
            }

            is AchievementRequirement.TotalPlayTime -> {
                val minutes = getTotalPlayTimeMinutes()
                minutes to (minutes >= requirement.minutes)
            }

            is AchievementRequirement.PlayStreak -> {
                val streak = getBestPlayStreak()
                streak to (streak >= requirement.days)
            }
        }
    }

    // Helper methods to query game statistics
    private suspend fun getCompletedGamesCount(): Int {
        return savedGameRepository.getAll().first().count { it.completed && !it.giveUp }
    }

    private suspend fun getCompletedGamesCountByDifficulty(difficulty: GameDifficulty): Int {
        val records = recordRepository.getAll().first()
        return records.count { it.difficulty == difficulty }
    }

    private suspend fun getCompletedGamesCountByType(gameType: GameType): Int {
        val records = recordRepository.getAll().first()
        return records.count { it.type == gameType }
    }

    private suspend fun getCompletedGamesNoMistakesCount(): Int {
        return savedGameRepository.getAll().first().count { it.completed && !it.giveUp && it.mistakes == 0 }
    }

    private suspend fun getCompletedGamesNoHintsCount(): Int {
        // Note: hintsUsed isn't tracked in SavedGame, so we'll count all completed games for now
        // This would need to be enhanced if hint tracking is added
        return savedGameRepository.getAll().first().count { it.completed && !it.giveUp }
    }

    private suspend fun hasCompletedUnderTime(difficulty: GameDifficulty, seconds: Int): Boolean {
        val records = recordRepository.getAll().first()
        return records.any { it.difficulty == difficulty && it.time.seconds <= seconds }
    }

    private suspend fun getCurrentDailyStreak(): Int {
        val completed = dailyChallengeRepository.getCompleted().first()
        return dailyChallengeManager.calculateCurrentStreak(completed.map { it.date })
    }

    private suspend fun getBestDailyStreak(): Int {
        val completed = dailyChallengeRepository.getCompleted().first()
        return dailyChallengeManager.calculateBestStreak(completed.map { it.date })
    }

    private suspend fun getDailyChallengesCompletedCount(): Int {
        return dailyChallengeRepository.getCompleted().first().size
    }

    private suspend fun getTotalPlayTimeMinutes(): Int {
        val savedGames = savedGameRepository.getAll().first()
        return savedGames.sumOf { it.timer.toMinutes() }.toInt()
    }

    private suspend fun getPlayStreak(): Int {
        val savedGames = savedGameRepository.getAll().first()
        val playDates =
            savedGames
                .filter { it.completed && !it.giveUp && it.finishedAt != null }
                .mapNotNull { it.finishedAt?.toLocalDate() }
                .distinct()
                .sorted()

        return calculateStreak(playDates)
    }

    private suspend fun getBestPlayStreak(): Int {
        val savedGames = savedGameRepository.getAll().first()
        val playDates =
            savedGames
                .filter { it.completed && !it.giveUp && it.finishedAt != null }
                .mapNotNull { it.finishedAt?.toLocalDate() }
                .distinct()
                .sorted()

        return calculateBestStreak(playDates)
    }

    private fun calculateStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val sortedDates = dates.sortedDescending()

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

    private fun calculateBestStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val sortedDates = dates.sorted()
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
