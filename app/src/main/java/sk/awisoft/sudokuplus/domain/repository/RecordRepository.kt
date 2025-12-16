package sk.awisoft.sudokuplus.domain.repository

import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.database.model.Record
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    suspend fun get(uid: Long): Record
    fun getAll(): Flow<List<Record>>
    fun getAllSortByTime(): Flow<List<Record>>
    fun getAll(difficulty: GameDifficulty, type: GameType): Flow<List<Record>>
    suspend fun insert(record: Record)
    suspend fun insert(records: List<Record>)
    suspend fun delete(record: Record)
}