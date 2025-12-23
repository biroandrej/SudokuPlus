package sk.awisoft.sudokuplus.data.database.repository

import java.time.ZonedDateTime
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.core.xp.XPConfig
import sk.awisoft.sudokuplus.data.database.dao.UserProgressDao
import sk.awisoft.sudokuplus.data.database.model.UserProgress
import sk.awisoft.sudokuplus.domain.repository.UserProgressRepository

class UserProgressRepositoryImpl(
    private val dao: UserProgressDao
) : UserProgressRepository {
    override fun get(): Flow<UserProgress?> = dao.get()

    override suspend fun getSync(): UserProgress? = dao.getSync()

    override suspend fun save(progress: UserProgress) = dao.insert(progress)

    override suspend fun addXP(xpAmount: Long): UserProgress {
        val current = dao.getSync() ?: UserProgress()
        val newTotalXP = current.totalXP + xpAmount

        // Calculate new level
        var level = current.level
        var xpForCurrentLevel = current.currentLevelXP + xpAmount
        var xpNeeded = current.xpToNextLevel

        // Level up loop
        while (xpForCurrentLevel >= xpNeeded) {
            xpForCurrentLevel -= xpNeeded
            level++
            xpNeeded = XPConfig.xpForLevel(level)
        }

        val updated =
            current.copy(
                totalXP = newTotalXP,
                level = level,
                currentLevelXP = xpForCurrentLevel,
                xpToNextLevel = xpNeeded,
                gamesForXP = current.gamesForXP + 1,
                lastXPEarnedAt = ZonedDateTime.now()
            )

        dao.insert(updated)
        return updated
    }

    override suspend fun reset() = dao.deleteAll()
}
