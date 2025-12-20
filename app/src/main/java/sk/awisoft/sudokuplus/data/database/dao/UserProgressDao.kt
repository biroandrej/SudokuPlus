package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.UserProgress

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun get(): Flow<UserProgress?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getSync(): UserProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: UserProgress)

    @Update
    suspend fun update(progress: UserProgress)

    @Query("DELETE FROM user_progress")
    suspend fun deleteAll()
}
