package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.ClaimedReward
import sk.awisoft.sudokuplus.data.database.model.LoginRewardStatus

@Dao
interface LoginRewardDao {
    @Query("SELECT * FROM login_reward_status WHERE id = 1")
    fun getStatus(): Flow<LoginRewardStatus?>

    @Query("SELECT * FROM login_reward_status WHERE id = 1")
    suspend fun getStatusSync(): LoginRewardStatus?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStatus(status: LoginRewardStatus)

    @Insert
    suspend fun insertClaimedReward(reward: ClaimedReward)

    @Query("SELECT * FROM claimed_rewards ORDER BY claimed_at DESC")
    fun getClaimedRewards(): Flow<List<ClaimedReward>>

    @Query("SELECT * FROM claimed_rewards ORDER BY claimed_at DESC LIMIT :limit")
    fun getRecentClaimedRewards(limit: Int): Flow<List<ClaimedReward>>

    @Query("DELETE FROM login_reward_status")
    suspend fun deleteStatus()

    @Query("DELETE FROM claimed_rewards")
    suspend fun deleteClaimedRewards()
}
