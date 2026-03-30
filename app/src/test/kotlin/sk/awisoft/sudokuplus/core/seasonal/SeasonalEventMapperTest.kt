package sk.awisoft.sudokuplus.core.seasonal

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventReward
import sk.awisoft.sudokuplus.core.seasonal.model.EventRewardType
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent
import sk.awisoft.sudokuplus.data.database.model.SeasonalEventEntity
import sk.awisoft.sudokuplus.data.database.repository.SeasonalEventMapper

class SeasonalEventMapperTest {

    @Test
    fun `toEntity should correctly map domain model to entity`() {
        val event = createTestEvent()
        val entity = SeasonalEventMapper.toEntity(event)

        assertEquals("test_event", entity.id)
        assertEquals("Test Event", entity.title)
        assertEquals("easter", entity.eventType)
        assertEquals(event.startDate.toEpochDay(), entity.startDate)
        assertEquals(event.endDate.toEpochDay(), entity.endDate)
        assertEquals(0xFFFF5722, entity.themePrimaryColor)
    }

    @Test
    fun `toDomain should correctly map entity to domain model`() {
        val startDate = LocalDate.of(2026, 4, 1)
        val endDate = LocalDate.of(2026, 4, 10)
        val entity = SeasonalEventEntity(
            id = "test_event",
            title = "Test Event",
            description = "A test event",
            eventType = "easter",
            startDate = startDate.toEpochDay(),
            endDate = endDate.toEpochDay(),
            themePrimaryColor = 0xFFFF5722,
            themeSecondaryColor = 0xFFFF9800,
            themeBackgroundColor = 0xFFFFF3E0,
            themeAccentColor = 0xFFE64A19,
            challengesJson = """[{"day":1,"difficulty":"easy","xpMultiplier":1.5}]""",
            rewardsJson = """[{"type":"hints","amount":3}]""",
            badgeId = "test_badge",
            syncedAt = System.currentTimeMillis()
        )

        val event = SeasonalEventMapper.toDomain(entity)

        assertEquals("test_event", event.id)
        assertEquals("Test Event", event.title)
        assertEquals(EventType.EASTER, event.eventType)
        assertEquals(startDate, event.startDate)
        assertEquals(endDate, event.endDate)
        assertEquals(1, event.challenges.size)
        assertEquals(GameDifficulty.Easy, event.challenges[0].difficulty)
        assertEquals(1.5, event.challenges[0].xpMultiplier, 0.01)
        assertEquals(1, event.rewards.size)
        assertEquals(EventRewardType.HINTS, event.rewards[0].type)
        assertEquals(3, event.rewards[0].amount)
    }

    @Test
    fun `round trip should preserve data`() {
        val original = createTestEvent()
        val entity = SeasonalEventMapper.toEntity(original)
        val restored = SeasonalEventMapper.toDomain(entity)

        assertEquals(original.id, restored.id)
        assertEquals(original.title, restored.title)
        assertEquals(original.eventType, restored.eventType)
        assertEquals(original.startDate, restored.startDate)
        assertEquals(original.endDate, restored.endDate)
        assertEquals(original.challenges.size, restored.challenges.size)
        assertEquals(original.rewards.size, restored.rewards.size)
        assertEquals(original.badgeId, restored.badgeId)
    }

    private fun createTestEvent(): SeasonalEvent = SeasonalEvent(
        id = "test_event",
        title = "Test Event",
        description = "A test event",
        eventType = EventType.EASTER,
        startDate = LocalDate.of(2026, 4, 1),
        endDate = LocalDate.of(2026, 4, 10),
        theme = EventTheme(
            primaryColor = 0xFFFF5722,
            secondaryColor = 0xFFFF9800,
            backgroundColor = 0xFFFFF3E0,
            accentColor = 0xFFE64A19
        ),
        challenges = listOf(
            EventChallenge(day = 1, difficulty = GameDifficulty.Easy, xpMultiplier = 1.5)
        ),
        rewards = listOf(
            EventReward(type = EventRewardType.HINTS, amount = 3)
        ),
        badgeId = "test_badge"
    )
}
