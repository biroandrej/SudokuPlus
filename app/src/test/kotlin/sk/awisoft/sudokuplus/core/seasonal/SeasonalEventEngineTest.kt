package sk.awisoft.sudokuplus.core.seasonal

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventReward
import sk.awisoft.sudokuplus.core.seasonal.model.EventRewardType
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent

class SeasonalEventEngineTest {

    private val engine = SeasonalEventEngine()

    @Test
    fun `getChallengeForDay should return correct challenge when day exists`() {
        val event = createEvent(challengeDays = 5)
        val challenge = engine.getChallengeForDay(event, 3)
        assertEquals(3, challenge?.day)
    }

    @Test
    fun `getChallengeForDay should return null when day exceeds event challenges`() {
        val event = createEvent(challengeDays = 5)
        val challenge = engine.getChallengeForDay(event, 6)
        assertEquals(null, challenge)
    }

    @Test
    fun `getChallengeForDay should return null for day zero`() {
        val event = createEvent(challengeDays = 5)
        val challenge = engine.getChallengeForDay(event, 0)
        assertEquals(null, challenge)
    }

    @Test
    fun `getCurrentEventDay should return 1 on start date`() {
        val today = LocalDate.now()
        val event = createEvent(
            startDate = today,
            endDate = today.plusDays(9),
            challengeDays = 10
        )
        assertEquals(1, engine.getCurrentEventDay(event))
    }

    @Test
    fun `getCurrentEventDay should return correct day mid-event`() {
        val today = LocalDate.now()
        val event = createEvent(
            startDate = today.minusDays(4),
            endDate = today.plusDays(5),
            challengeDays = 10
        )
        assertEquals(5, engine.getCurrentEventDay(event))
    }

    @Test
    fun `getCurrentEventDay should return 0 for upcoming event`() {
        val today = LocalDate.now()
        val event = createEvent(
            startDate = today.plusDays(3),
            endDate = today.plusDays(12),
            challengeDays = 10
        )
        assertEquals(0, engine.getCurrentEventDay(event))
    }

    @Test
    fun `getCurrentEventDay should return duration for ended event`() {
        val today = LocalDate.now()
        val event = createEvent(
            startDate = today.minusDays(12),
            endDate = today.minusDays(3),
            challengeDays = 10
        )
        assertEquals(10, engine.getCurrentEventDay(event))
    }

    @Test
    fun `canParticipate should return true for active event`() {
        val event = createEvent(
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now().plusDays(5)
        )
        assertTrue(engine.canParticipate(event))
    }

    @Test
    fun `canParticipate should return false for ended event`() {
        val event = createEvent(
            startDate = LocalDate.now().minusDays(10),
            endDate = LocalDate.now().minusDays(1)
        )
        assertFalse(engine.canParticipate(event))
    }

    @Test
    fun `canParticipate should return false for upcoming event`() {
        val event = createEvent(
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(10)
        )
        assertFalse(engine.canParticipate(event))
    }

    @Test
    fun `getEventSeed should be deterministic for same event and day`() {
        val event = createEvent()
        val seed1 = engine.getEventSeed(event, 3)
        val seed2 = engine.getEventSeed(event, 3)
        assertEquals(seed1, seed2)
    }

    @Test
    fun `getEventSeed should differ for different days`() {
        val event = createEvent()
        val seed1 = engine.getEventSeed(event, 1)
        val seed2 = engine.getEventSeed(event, 2)
        assertTrue(seed1 != seed2)
    }

    @Test
    fun `getCompletionPercentage should calculate correctly`() {
        val event = createEvent(challengeDays = 10)
        assertEquals(0, engine.getCompletionPercentage(event, 0))
        assertEquals(50, engine.getCompletionPercentage(event, 5))
        assertEquals(100, engine.getCompletionPercentage(event, 10))
    }

    private fun createEvent(
        startDate: LocalDate = LocalDate.now().minusDays(2),
        endDate: LocalDate = LocalDate.now().plusDays(7),
        challengeDays: Int = 10
    ): SeasonalEvent = SeasonalEvent(
        id = "test_event",
        title = "Test Event",
        description = "A test seasonal event",
        eventType = EventType.EASTER,
        startDate = startDate,
        endDate = endDate,
        theme = EventTheme(
            primaryColor = 0xFFFF5722,
            secondaryColor = 0xFFFF9800,
            backgroundColor = 0xFFFFF3E0,
            accentColor = 0xFFE64A19
        ),
        challenges = (1..challengeDays).map { day ->
            EventChallenge(
                day = day,
                difficulty = GameDifficulty.entries[
                    (day % 4) + 2 // cycles Easy, Moderate, Hard, Challenge
                ],
                xpMultiplier = 1.5
            )
        },
        rewards = listOf(
            EventReward(type = EventRewardType.HINTS, amount = 3)
        ),
        badgeId = "test_badge"
    )
}
