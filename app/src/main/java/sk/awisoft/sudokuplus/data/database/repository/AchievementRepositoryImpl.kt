package sk.awisoft.sudokuplus.data.database.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.dao.UserAchievementDao
import sk.awisoft.sudokuplus.data.database.model.UserAchievement
import sk.awisoft.sudokuplus.domain.repository.AchievementRepository
import java.time.ZonedDateTime

class AchievementRepositoryImpl(
    private val dao: UserAchievementDao
) : AchievementRepository {

    override fun getAll(): Flow<List<UserAchievement>> = dao.getAll()

    override suspend fun get(achievementId: String): UserAchievement? = dao.get(achievementId)

    override fun getFlow(achievementId: String): Flow<UserAchievement?> = dao.getFlow(achievementId)

    override fun getUnlocked(): Flow<List<UserAchievement>> = dao.getUnlocked()

    override fun getUnlockedCount(): Flow<Int> = dao.getUnlockedCount()

    override suspend fun save(achievement: UserAchievement) = dao.insert(achievement)

    override suspend fun saveAll(achievements: List<UserAchievement>) = dao.insertAll(achievements)

    override suspend fun updateProgress(achievementId: String, progress: Int) {
        val existing = dao.get(achievementId)
        if (existing != null) {
            if (existing.unlockedAt == null) {
                dao.update(existing.copy(progress = progress))
            }
        } else {
            dao.insert(UserAchievement(achievementId = achievementId, progress = progress))
        }
    }

    override suspend fun unlock(achievementId: String) {
        val existing = dao.get(achievementId)
        if (existing != null) {
            if (existing.unlockedAt == null) {
                dao.update(existing.copy(unlockedAt = ZonedDateTime.now()))
            }
        } else {
            dao.insert(
                UserAchievement(
                    achievementId = achievementId,
                    progress = 0,
                    unlockedAt = ZonedDateTime.now()
                )
            )
        }
    }
}
