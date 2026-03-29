package sk.awisoft.sudokuplus.core.seasonal

import sk.awisoft.sudokuplus.R
import sk.awisoft.sudokuplus.core.seasonal.model.EventBadgeDefinition
import sk.awisoft.sudokuplus.core.seasonal.model.EventType

object EventBadgeDefinitions {
    val all: List<EventBadgeDefinition> = listOf(
        EventBadgeDefinition(
            id = "event_easter",
            nameRes = R.string.badge_event_easter_name,
            descriptionRes = R.string.badge_event_easter_desc,
            iconRes = R.drawable.ic_badge_monthly,
            eventType = EventType.EASTER
        ),
        EventBadgeDefinition(
            id = "event_summer",
            nameRes = R.string.badge_event_summer_name,
            descriptionRes = R.string.badge_event_summer_desc,
            iconRes = R.drawable.ic_badge_monthly,
            eventType = EventType.SUMMER
        ),
        EventBadgeDefinition(
            id = "event_halloween",
            nameRes = R.string.badge_event_halloween_name,
            descriptionRes = R.string.badge_event_halloween_desc,
            iconRes = R.drawable.ic_badge_monthly,
            eventType = EventType.HALLOWEEN
        ),
        EventBadgeDefinition(
            id = "event_christmas",
            nameRes = R.string.badge_event_christmas_name,
            descriptionRes = R.string.badge_event_christmas_desc,
            iconRes = R.drawable.ic_badge_monthly,
            eventType = EventType.CHRISTMAS
        ),
        EventBadgeDefinition(
            id = "event_new_year",
            nameRes = R.string.badge_event_new_year_name,
            descriptionRes = R.string.badge_event_new_year_desc,
            iconRes = R.drawable.ic_badge_monthly,
            eventType = EventType.NEW_YEAR
        )
    )

    fun getByEventType(eventType: EventType): EventBadgeDefinition? =
        all.find { it.eventType == eventType }

    fun getById(id: String): EventBadgeDefinition? =
        all.find { it.id == id }
}
