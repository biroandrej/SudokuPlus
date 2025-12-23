package sk.awisoft.sudokuplus.domain.repository

import java.time.Duration
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge

interface DailyChallengeRepository {
    suspend fun getToday(): DailyChallenge?

    fun getTodayFlow(): Flow<DailyChallenge?>

    suspend fun get(date: LocalDate): DailyChallenge?

    fun getFlow(date: LocalDate): Flow<DailyChallenge?>

    fun getCompleted(): Flow<List<DailyChallenge>>

    fun getInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyChallenge>>

    fun getCompletedCount(): Flow<Int>

    fun getAll(): Flow<List<DailyChallenge>>

    suspend fun save(challenge: DailyChallenge)

    suspend fun insertAll(challenges: List<DailyChallenge>)

    suspend fun update(challenge: DailyChallenge)

    suspend fun updateProgress(
        date: LocalDate,
        currentBoard: String,
        notes: String?,
        mistakes: Int,
        hintsUsed: Int
    )

    suspend fun complete(date: LocalDate, completionTime: Duration, mistakes: Int, hintsUsed: Int)
}
