package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.model.EventProgressEntity

interface SeasonalEventRepository {
    fun getActiveEvents(): Flow<List<SeasonalEvent>>

    fun getUpcomingEvents(): Flow<List<SeasonalEvent>>

    fun getAllEvents(): Flow<List<SeasonalEvent>>

    suspend fun getEventById(id: String): SeasonalEvent?

    suspend fun syncEvents()

    suspend fun getEventProgress(eventId: String): EventProgressEntity?

    fun getEventProgressFlow(eventId: String): Flow<EventProgressEntity?>

    suspend fun updateEventProgress(progress: EventProgressEntity)
}
