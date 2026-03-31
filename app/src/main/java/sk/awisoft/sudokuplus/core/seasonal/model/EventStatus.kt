package sk.awisoft.sudokuplus.core.seasonal.model

sealed class EventStatus {
    data object Upcoming : EventStatus()
    data object Active : EventStatus()
    data object Ended : EventStatus()
}
