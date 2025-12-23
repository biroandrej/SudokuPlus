package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge

@Dao
interface DailyChallengeDao {
    @Query("SELECT * FROM daily_challenge WHERE date = :date")
    suspend fun get(date: LocalDate): DailyChallenge?

    @Query("SELECT * FROM daily_challenge WHERE date = :date")
    fun getFlow(date: LocalDate): Flow<DailyChallenge?>

    @Query("SELECT * FROM daily_challenge WHERE completed_at IS NOT NULL ORDER BY date DESC")
    fun getCompleted(): Flow<List<DailyChallenge>>

    @Query("SELECT * FROM daily_challenge WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getInRange(startDate: LocalDate, endDate: LocalDate): Flow<List<DailyChallenge>>

    @Query("SELECT COUNT(*) FROM daily_challenge WHERE completed_at IS NOT NULL")
    fun getCompletedCount(): Flow<Int>

    @Query("SELECT * FROM daily_challenge ORDER BY date DESC")
    fun getAll(): Flow<List<DailyChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(challenge: DailyChallenge)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(challenges: List<DailyChallenge>)

    @Update
    suspend fun update(challenge: DailyChallenge)

    @Query("DELETE FROM daily_challenge WHERE date = :date")
    suspend fun delete(date: LocalDate)
}
