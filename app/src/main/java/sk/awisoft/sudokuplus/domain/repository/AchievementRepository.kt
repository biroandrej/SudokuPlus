package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.UserAchievement

interface AchievementRepository {
    fun getAll(): Flow<List<UserAchievement>>

    suspend fun get(achievementId: String): UserAchievement?

    fun getFlow(achievementId: String): Flow<UserAchievement?>

    fun getUnlocked(): Flow<List<UserAchievement>>

    fun getUnlockedCount(): Flow<Int>

    suspend fun save(achievement: UserAchievement)

    suspend fun saveAll(achievements: List<UserAchievement>)

    suspend fun updateProgress(achievementId: String, progress: Int)

    suspend fun unlock(achievementId: String)
}
