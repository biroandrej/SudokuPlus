package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.UserAchievement

@Dao
interface UserAchievementDao {
    @Query("SELECT * FROM user_achievement")
    fun getAll(): Flow<List<UserAchievement>>

    @Query("SELECT * FROM user_achievement WHERE achievement_id = :achievementId")
    suspend fun get(achievementId: String): UserAchievement?

    @Query("SELECT * FROM user_achievement WHERE achievement_id = :achievementId")
    fun getFlow(achievementId: String): Flow<UserAchievement?>

    @Query("SELECT * FROM user_achievement WHERE unlocked_at IS NOT NULL")
    fun getUnlocked(): Flow<List<UserAchievement>>

    @Query("SELECT COUNT(*) FROM user_achievement WHERE unlocked_at IS NOT NULL")
    fun getUnlockedCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: UserAchievement)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<UserAchievement>)

    @Update
    suspend fun update(achievement: UserAchievement)

    @Query("DELETE FROM user_achievement")
    suspend fun deleteAll()
}
