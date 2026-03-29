package sk.awisoft.sudokuplus.data.database.repository

import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventReward
import sk.awisoft.sudokuplus.core.seasonal.model.EventRewardType
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.dao.SeasonalEventDao
import sk.awisoft.sudokuplus.data.database.model.EventChallengeGame
import sk.awisoft.sudokuplus.data.database.model.EventProgressEntity
import sk.awisoft.sudokuplus.domain.repository.SeasonalEventRepository

class SeasonalEventRepositoryImpl(
    private val dao: SeasonalEventDao,
    private val firestore: FirebaseFirestore
) : SeasonalEventRepository {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getActiveEvents(): Flow<List<SeasonalEvent>> =
        flow { emit(LocalDate.now().toEpochDay()) }
            .flatMapLatest { now ->
                dao.getActiveEvents(now).map { entities ->
                    entities.map { SeasonalEventMapper.toDomain(it) }
                }
            }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getUpcomingEvents(): Flow<List<SeasonalEvent>> =
        flow { emit(LocalDate.now().toEpochDay()) }
            .flatMapLatest { now ->
                dao.getUpcomingEvents(now).map { entities ->
                    entities.map { SeasonalEventMapper.toDomain(it) }
                }
            }

    override fun getAllEvents(): Flow<List<SeasonalEvent>> = dao.getAllEvents().map { entities ->
        entities.map { SeasonalEventMapper.toDomain(it) }
    }

    override suspend fun getEventById(id: String): SeasonalEvent? =
        dao.getEventById(id)?.let { SeasonalEventMapper.toDomain(it) }

    override suspend fun syncEvents() {
        val snapshot = firestore.collection(EVENTS_COLLECTION).get().await()
        val events = snapshot.documents.mapNotNull { doc ->
            try {
                parseFirestoreEvent(doc.id, doc.data ?: return@mapNotNull null)
            } catch (e: Exception) {
                null
            }
        }
        val entities = events.map { SeasonalEventMapper.toEntity(it) }
        dao.insertAll(entities)
    }

    override suspend fun getEventProgress(eventId: String): EventProgressEntity? =
        dao.getProgress(eventId)

    override fun getEventProgressFlow(eventId: String): Flow<EventProgressEntity?> =
        dao.getProgressFlow(eventId)

    override suspend fun updateEventProgress(progress: EventProgressEntity) {
        val existing = dao.getProgress(progress.eventId)
        if (existing != null) {
            dao.updateProgress(progress)
        } else {
            dao.insertProgress(progress)
        }
    }

    override suspend fun getCompletedChallengesCount(): Int = dao.getCompletedChallengesCount()

    override suspend fun getParticipatedEventsCount(): Int = dao.getParticipatedEventsCount()

    override suspend fun getChallengeGameByBoardUid(boardUid: Long): EventChallengeGame? =
        dao.getChallengeGameByBoardUid(boardUid)

    override suspend fun markChallengeCompleted(boardUid: Long) =
        dao.markChallengeCompleted(boardUid)

    override suspend fun getChallengeGames(eventId: String): List<EventChallengeGame> =
        dao.getChallengeGames(eventId)

    override fun getChallengeGamesFlow(eventId: String): Flow<List<EventChallengeGame>> =
        dao.getChallengeGamesFlow(eventId)

    override suspend fun insertChallengeGame(game: EventChallengeGame): Long =
        dao.insertChallengeGame(game)

    @Suppress("UNCHECKED_CAST")
    private fun parseFirestoreEvent(id: String, data: Map<String, Any>): SeasonalEvent {
        val challengesList = (data["challenges"] as? List<Map<String, Any>>)?.map { c ->
            EventChallenge(
                day = (c["day"] as Long).toInt(),
                difficulty = GameDifficulty.get(c["difficulty"] as String),
                xpMultiplier = (c["xpMultiplier"] as? Double) ?: 1.5
            )
        } ?: emptyList()

        val rewardsList = (data["rewards"] as? List<Map<String, Any>>)?.map { r ->
            EventReward(
                type = EventRewardType.get(r["type"] as String),
                amount = (r["amount"] as Long).toInt()
            )
        } ?: emptyList()

        val themeMap = data["theme"] as? Map<String, Any> ?: emptyMap()

        return SeasonalEvent(
            id = id,
            title = data["title"] as String,
            description = data["description"] as String,
            eventType = EventType.get(data["eventType"] as String),
            startDate = LocalDate.parse(data["startDate"] as String),
            endDate = LocalDate.parse(data["endDate"] as String),
            theme = EventTheme(
                primaryColor = (themeMap["primaryColor"] as? Long) ?: 0xFF000000,
                secondaryColor = (themeMap["secondaryColor"] as? Long) ?: 0xFF000000,
                backgroundColor = (themeMap["backgroundColor"] as? Long) ?: 0xFFFFFFFF,
                accentColor = (themeMap["accentColor"] as? Long) ?: 0xFF000000
            ),
            challenges = challengesList,
            rewards = rewardsList,
            badgeId = (data["badgeId"] as? String) ?: "${id}_badge"
        )
    }

    companion object {
        private const val EVENTS_COLLECTION = "seasonal_events"
    }
}
