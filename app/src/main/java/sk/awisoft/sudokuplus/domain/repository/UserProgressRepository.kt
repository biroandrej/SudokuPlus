package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.UserProgress

interface UserProgressRepository {
    fun get(): Flow<UserProgress?>

    suspend fun getSync(): UserProgress?

    suspend fun save(progress: UserProgress)

    suspend fun addXP(xpAmount: Long): UserProgress

    suspend fun reset()
}
