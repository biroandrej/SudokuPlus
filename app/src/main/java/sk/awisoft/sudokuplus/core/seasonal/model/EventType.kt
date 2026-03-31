package sk.awisoft.sudokuplus.core.seasonal.model

enum class EventType(val value: String) {
    EASTER("easter"),
    SUMMER("summer"),
    HALLOWEEN("halloween"),
    CHRISTMAS("christmas"),
    NEW_YEAR("new_year");

    companion object {
        fun find(value: String): EventType? = entries.find { it.value == value }

        fun get(value: String): EventType =
            find(value) ?: throw IllegalArgumentException("Unknown EventType: $value")
    }
}
