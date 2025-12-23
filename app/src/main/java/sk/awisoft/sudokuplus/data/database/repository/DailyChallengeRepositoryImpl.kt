package sk.awisoft.sudokuplus.data.database.repository

import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.dao.DailyChallengeDao
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository

class DailyChallengeRepositoryImpl(
    private val dao: DailyChallengeDao
) : DailyChallengeRepository {
    override suspend fun getToday(): DailyChallenge? = dao.get(LocalDate.now())

    override fun getTodayFlow(): Flow<DailyChallenge?> = dao.getFlow(LocalDate.now())

    override suspend fun get(date: LocalDate): DailyChallenge? = dao.get(date)

    override fun getFlow(date: LocalDate): Flow<DailyChallenge?> = dao.getFlow(date)

    override fun getCompleted(): Flow<List<DailyChallenge>> = dao.getCompleted()

    override fun getInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyChallenge>> =
        dao.getInRange(startDate, endDate)

    override fun getCompletedCount(): Flow<Int> = dao.getCompletedCount()

    override fun getAll(): Flow<List<DailyChallenge>> = dao.getAll()

    override suspend fun save(challenge: DailyChallenge) = dao.insert(challenge)

    override suspend fun insertAll(challenges: List<DailyChallenge>) = dao.insertAll(challenges)

    override suspend fun update(challenge: DailyChallenge) = dao.update(challenge)

    override suspend fun updateProgress(
        date: LocalDate,
        currentBoard: String,
        notes: String?,
        mistakes: Int,
        hintsUsed: Int
    ) {
        dao.get(date)?.let { challenge ->
            dao.update(
                challenge.copy(
                    currentBoard = currentBoard,
                    notes = notes,
                    mistakes = mistakes,
                    hintsUsed = hintsUsed
                )
            )
        }
    }

    override suspend fun complete(
        date: LocalDate,
        completionTime: Duration,
        mistakes: Int,
        hintsUsed: Int
    ) {
        dao.get(date)?.let { challenge ->
            dao.update(
                challenge.copy(
                    completedAt = ZonedDateTime.now(),
                    completionTime = completionTime,
                    mistakes = mistakes,
                    hintsUsed = hintsUsed
                )
            )
        }
    }
}
