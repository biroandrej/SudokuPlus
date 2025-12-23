package sk.awisoft.sudokuplus.data.database.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.domain.repository.RecordRepository

class RecordRepositoryImpl(
    private val recordDao: RecordDao
) : RecordRepository {
    override suspend fun get(uid: Long): Record = recordDao.get(uid)

    override fun getAll(): Flow<List<Record>> = recordDao.getAll()

    override fun getAllSortByTime(): Flow<List<Record>> = recordDao.getAllSortByTime()

    override fun getAll(difficulty: GameDifficulty, type: GameType) =
        recordDao.getAll(difficulty, type)

    override suspend fun insert(record: Record) = recordDao.insert(record)

    override suspend fun insert(records: List<Record>) = recordDao.insert(records)

    override suspend fun delete(record: Record) = recordDao.delete(record)
}
