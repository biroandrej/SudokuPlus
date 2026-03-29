package sk.awisoft.sudokuplus.core.seasonal

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import sk.awisoft.sudokuplus.core.seasonal.model.EventChallenge
import sk.awisoft.sudokuplus.core.seasonal.model.SeasonalEvent

@Singleton
class SeasonalEventEngine @Inject constructor() {

    fun getChallengeForDay(event: SeasonalEvent, day: Int): EventChallenge? =
        event.challenges.find { it.day == day }

    fun getCurrentEventDay(event: SeasonalEvent): Int {
        val today = LocalDate.now()
        return when {
            today.isBefore(event.startDate) -> 0
            today.isAfter(event.endDate) -> event.durationDays
            else -> (ChronoUnit.DAYS.between(event.startDate, today) + 1).toInt()
        }
    }

    fun getEventSeed(event: SeasonalEvent, day: Int): Long =
        event.id.hashCode().toLong() * 1_000_000L + day * 7919L

    fun getCompletionPercentage(event: SeasonalEvent, challengesCompleted: Int): Int {
        if (event.challenges.isEmpty()) return 0
        return (challengesCompleted * 100) / event.challenges.size
    }
}
