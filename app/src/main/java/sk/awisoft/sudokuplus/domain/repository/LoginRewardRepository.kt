package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.ClaimedReward
import sk.awisoft.sudokuplus.data.database.model.LoginRewardStatus

interface LoginRewardRepository {
    fun getStatus(): Flow<LoginRewardStatus?>
    suspend fun getStatusSync(): LoginRewardStatus?
    suspend fun updateStatus(status: LoginRewardStatus)
    suspend fun recordClaimedReward(reward: ClaimedReward)
    fun getClaimedRewards(): Flow<List<ClaimedReward>>
    suspend fun reset()
}
