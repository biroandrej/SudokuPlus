package sk.awisoft.sudokuplus.core.seasonal

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.seasonal.model.EventBadgeDefinition
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventReward
import sk.awisoft.sudokuplus.core.seasonal.model.EventRewardType
import sk.awisoft.sudokuplus.core.seasonal.model.EventStatus
import sk.awisoft.sudokuplus.core.seasonal.model.EventTheme
import sk.awisoft.sudokuplus.core.seasonal.model.EventType
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent

class SeasonalEventModelsTest {

    @Test
    fun `event status should be Upcoming when start date is in the future`() {
        val event = createEvent(
            startDate = LocalDate.now().plusDays(5),
            endDate = LocalDate.now().plusDays(15)
        )
        assertEquals(EventStatus.Upcoming, event.status)
    }

    @Test
    fun `event status should be Active when current date is between start and end`() {
        val event = createEvent(
            startDate = LocalDate.now().minusDays(2),
            endDate = LocalDate.now().plusDays(5)
        )
        assertEquals(EventStatus.Active, event.status)
    }

    @Test
    fun `event status should be Ended when end date is in the past`() {
        val event = createEvent(
            startDate = LocalDate.now().minusDays(15),
            endDate = LocalDate.now().minusDays(1)
        )
        assertEquals(EventStatus.Ended, event.status)
    }

    @Test
    fun `event status should be Active on start date`() {
        val event = createEvent(
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(5)
        )
        assertEquals(EventStatus.Active, event.status)
    }

    @Test
    fun `event status should be Active on end date`() {
        val event = createEvent(
            startDate = LocalDate.now().minusDays(5),
            endDate = LocalDate.now()
        )
        assertEquals(EventStatus.Active, event.status)
    }

    @Test
    fun `event duration days should be calculated correctly`() {
        val event = createEvent(
            startDate = LocalDate.of(2026, 4, 1),
            endDate = LocalDate.of(2026, 4, 10)
        )
        assertEquals(10, event.durationDays)
    }

    @Test
    fun `event challenge should have correct properties`() {
        val challenge = EventChallenge(
            day = 1,
            difficulty = GameDifficulty.Moderate,
            xpMultiplier = 1.5
        )
        assertEquals(1, challenge.day)
        assertEquals(GameDifficulty.Moderate, challenge.difficulty)
        assertEquals(1.5, challenge.xpMultiplier, 0.01)
    }

    @Test
    fun `event theme should have correct color values`() {
        val theme = EventTheme(
            primaryColor = 0xFFFF5722,
            secondaryColor = 0xFFFF9800,
            backgroundColor = 0xFFFFF3E0,
            accentColor = 0xFFE64A19
        )
        assertEquals(0xFFFF5722, theme.primaryColor)
        assertEquals(0xFFFF9800, theme.secondaryColor)
    }

    @Test
    fun `event reward types should be distinguishable`() {
        val hintReward = EventReward(type = EventRewardType.HINTS, amount = 3)
        val xpReward = EventReward(type = EventRewardType.XP_BOOST, amount = 2)
        val badgeReward = EventReward(type = EventRewardType.EVENT_BADGE, amount = 1)

        assertEquals(EventRewardType.HINTS, hintReward.type)
        assertEquals(EventRewardType.XP_BOOST, xpReward.type)
        assertEquals(EventRewardType.EVENT_BADGE, badgeReward.type)
    }

    @Test
    fun `event types should cover all seasons`() {
        val types = EventType.entries
        assertTrue(types.contains(EventType.EASTER))
        assertTrue(types.contains(EventType.SUMMER))
        assertTrue(types.contains(EventType.HALLOWEEN))
        assertTrue(types.contains(EventType.CHRISTMAS))
        assertTrue(types.contains(EventType.NEW_YEAR))
    }

    @Test
    fun `event badge definition should have id and rarity`() {
        val badge = EventBadgeDefinition(
            id = "easter_2026",
            nameRes = 0,
            descriptionRes = 0,
            iconRes = 0,
            eventType = EventType.EASTER
        )
        assertEquals("easter_2026", badge.id)
        assertEquals(EventType.EASTER, badge.eventType)
    }

    @Test
    fun `event isActive should return true only for active events`() {
        val activeEvent = createEvent(
            startDate = LocalDate.now().minusDays(1),
            endDate = LocalDate.now().plusDays(5)
        )
        val upcomingEvent = createEvent(
            startDate = LocalDate.now().plusDays(1),
            endDate = LocalDate.now().plusDays(10)
        )
        assertTrue(activeEvent.isActive)
        assertFalse(upcomingEvent.isActive)
    }

    private fun createEvent(startDate: LocalDate, endDate: LocalDate): SeasonalEvent =
        SeasonalEvent(
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
            challenges = listOf(
                EventChallenge(day = 1, difficulty = GameDifficulty.Easy, xpMultiplier = 1.5)
            ),
            rewards = listOf(
                EventReward(type = EventRewardType.HINTS, amount = 3)
            ),
            badgeId = "test_badge"
        )
}
