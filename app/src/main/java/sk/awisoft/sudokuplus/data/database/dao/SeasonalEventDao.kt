package sk.awisoft.sudokuplus.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.EventProgressEntity
import sk.awisoft.sudokuplus.data.database.model.SeasonalEventEntity

@Dao
interface SeasonalEventDao {
    @Query("SELECT * FROM seasonal_events WHERE :now BETWEEN start_date AND end_date")
    fun getActiveEvents(now: Long): Flow<List<SeasonalEventEntity>>

    @Query("SELECT * FROM seasonal_events WHERE start_date > :now")
    fun getUpcomingEvents(now: Long): Flow<List<SeasonalEventEntity>>

    @Query("SELECT * FROM seasonal_events WHERE end_date < :now")
    fun getEndedEvents(now: Long): Flow<List<SeasonalEventEntity>>

    @Query("SELECT * FROM seasonal_events WHERE id = :id")
    suspend fun getEventById(id: String): SeasonalEventEntity?

    @Query("SELECT * FROM seasonal_events ORDER BY start_date ASC")
    fun getAllEvents(): Flow<List<SeasonalEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<SeasonalEventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: SeasonalEventEntity)

    @Query("DELETE FROM seasonal_events WHERE id = :id")
    suspend fun delete(id: String)

    // Event Progress
    @Query("SELECT * FROM event_progress WHERE event_id = :eventId")
    suspend fun getProgress(eventId: String): EventProgressEntity?

    @Query("SELECT * FROM event_progress WHERE event_id = :eventId")
    fun getProgressFlow(eventId: String): Flow<EventProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: EventProgressEntity)

    @Update
    suspend fun updateProgress(progress: EventProgressEntity)
}
