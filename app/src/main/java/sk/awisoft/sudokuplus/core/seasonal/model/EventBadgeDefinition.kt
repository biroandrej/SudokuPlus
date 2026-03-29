package sk.awisoft.sudokuplus.core.seasonal.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class EventBadgeDefinition(
    val id: String,
    @StringRes val nameRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val iconRes: Int,
    val eventType: EventType
)
