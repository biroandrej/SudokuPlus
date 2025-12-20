package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.RewardBadge

@Dao
interface RewardBadgeDao {
    @Query("SELECT * FROM reward_badges ORDER BY earned_at DESC")
    fun getAll(): Flow<List<RewardBadge>>

    @Query("SELECT * FROM reward_badges WHERE badge_id = :badgeId")
    suspend fun get(badgeId: String): RewardBadge?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: RewardBadge)

    @Query("DELETE FROM reward_badges")
    suspend fun deleteAll()
}
