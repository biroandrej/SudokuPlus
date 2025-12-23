package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.database.model.Record

@Dao
interface RecordDao {
    @Query("SELECT * FROM record WHERE board_uid == :uid")
    suspend fun get(uid: Long): Record

    @Query("SELECT * FROM record")
    fun getAll(): Flow<List<Record>>

    @Query(
        "SELECT * FROM record " +
            "WHERE type == :type and difficulty == :difficulty " +
            "ORDER BY time ASC"
    )
    fun getAll(difficulty: GameDifficulty, type: GameType): Flow<List<Record>>

    @Query("SELECT * FROM record ORDER BY time ASC")
    fun getAllSortByTime(): Flow<List<Record>>

    @Delete
    suspend fun delete(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: Record)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(records: List<Record>)
}
