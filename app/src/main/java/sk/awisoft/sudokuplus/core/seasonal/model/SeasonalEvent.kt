package sk.awisoft.sudokuplus.core.seasonal.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class SeasonalEvent(
    val id: String,
    val title: String,
    val description: String,
    val eventType: EventType,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val theme: EventTheme,
    val challenges: List<EventChallenge>,
    val rewards: List<EventReward>,
    val badgeId: String
) {
    val status: EventStatus
        get() {
            val today = LocalDate.now()
            return when {
                today.isBefore(startDate) -> EventStatus.Upcoming
                today.isAfter(endDate) -> EventStatus.Ended
                else -> EventStatus.Active
            }
        }

    val isActive: Boolean
        get() = status is EventStatus.Active

    val durationDays: Int
        get() = (ChronoUnit.DAYS.between(startDate, endDate) + 1).toInt()

    val daysLeft: Int
        get() = ChronoUnit.DAYS.between(LocalDate.now(), endDate).toInt()

    val timeProgress: Float
        get() {
            val total = ChronoUnit.DAYS.between(startDate, endDate).toFloat()
            if (total <= 0f) return 0f
            return (ChronoUnit.DAYS.between(startDate, LocalDate.now()).toFloat() / total)
                .coerceIn(0f, 1f)
        }
}
