package sk.awisoft.sudokuplus.data.database.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.dao.LoginRewardDao
import sk.awisoft.sudokuplus.data.database.model.ClaimedReward
import sk.awisoft.sudokuplus.data.database.model.LoginRewardStatus
import sk.awisoft.sudokuplus.domain.repository.LoginRewardRepository

class LoginRewardRepositoryImpl(
    private val dao: LoginRewardDao
) : LoginRewardRepository {

    override fun getStatus(): Flow<LoginRewardStatus?> = dao.getStatus()

    override suspend fun getStatusSync(): LoginRewardStatus? = dao.getStatusSync()

    override suspend fun updateStatus(status: LoginRewardStatus) = dao.updateStatus(status)

    override suspend fun recordClaimedReward(reward: ClaimedReward) = dao.insertClaimedReward(reward)

    override fun getClaimedRewards(): Flow<List<ClaimedReward>> = dao.getClaimedRewards()

    override suspend fun reset() {
        dao.deleteStatus()
        dao.deleteClaimedRewards()
    }
}
