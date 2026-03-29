package sk.awisoft.sudokuplus.core.seasonal

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.EventType

class EventChallengeManagerTest {

    private val manager = EventChallengeManager()

    @Test
    fun `generatePuzzle should produce valid puzzle data`() {
        val challenge = EventChallenge(
            day = 1,
            difficulty = GameDifficulty.Easy,
            xpMultiplier = 1.5
        )
        val seed = 123456L
        val result = manager.generatePuzzle(challenge, seed)

        assertNotNull(result)
        assertEquals(GameType.Default9x9, result.gameType)
        assertTrue(result.initialBoard.isNotEmpty())
        assertTrue(result.solvedBoard.isNotEmpty())
        assertTrue(result.initialBoard != result.solvedBoard)
    }

    @Test
    fun `generatePuzzle should be deterministic for same seed`() {
        val challenge = EventChallenge(
            day = 1,
            difficulty = GameDifficulty.Moderate,
            xpMultiplier = 1.5
        )
        val seed = 789012L
        val result1 = manager.generatePuzzle(challenge, seed)
        val result2 = manager.generatePuzzle(challenge, seed)

        assertEquals(result1.initialBoard, result2.initialBoard)
        assertEquals(result1.solvedBoard, result2.solvedBoard)
    }

    @Test
    fun `generatePuzzle should produce different puzzles for different seeds`() {
        val challenge = EventChallenge(
            day = 1,
            difficulty = GameDifficulty.Easy,
            xpMultiplier = 1.5
        )
        val result1 = manager.generatePuzzle(challenge, 100L)
        val result2 = manager.generatePuzzle(challenge, 200L)

        assertTrue(result1.initialBoard != result2.initialBoard)
    }

    @Test
    fun `getDefaultChallenges should create challenges for event duration`() {
        val challenges = manager.getDefaultChallenges(
            durationDays = 7,
            eventType = EventType.EASTER
        )
        assertEquals(7, challenges.size)
        assertEquals(1, challenges.first().day)
        assertEquals(7, challenges.last().day)
    }

    @Test
    fun `getDefaultChallenges should cycle through difficulties`() {
        val challenges = manager.getDefaultChallenges(
            durationDays = 4,
            eventType = EventType.HALLOWEEN
        )
        assertEquals(GameDifficulty.Easy, challenges[0].difficulty)
        assertEquals(GameDifficulty.Moderate, challenges[1].difficulty)
        assertEquals(GameDifficulty.Hard, challenges[2].difficulty)
        assertEquals(GameDifficulty.Challenge, challenges[3].difficulty)
    }

    @Test
    fun `getDefaultChallenges should set event xp multiplier`() {
        val challenges = manager.getDefaultChallenges(
            durationDays = 3,
            eventType = EventType.CHRISTMAS
        )
        challenges.forEach { challenge ->
            assertEquals(EventChallengeManager.EVENT_XP_MULTIPLIER, challenge.xpMultiplier, 0.01)
        }
    }
}
