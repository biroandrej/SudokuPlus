package sk.awisoft.sudokuplus.core.seasonal.model

enum class EventRewardType(val value: String) {
    HINTS("hints"),
    XP_BOOST("xp_boost"),
    EVENT_BADGE("event_badge");

    companion object {
        fun find(value: String): EventRewardType? = entries.find { it.value == value }

        fun get(value: String): EventRewardType =
            find(value) ?: throw IllegalArgumentException("Unknown EventRewardType: $value")
    }
}

data class EventReward(
    val type: EventRewardType,
    val amount: Int
)
