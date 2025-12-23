package sk.awisoft.sudokuplus.data.database.converters

import androidx.room.TypeConverter
import sk.awisoft.sudokuplus.core.reward.RewardType

class RewardTypeConverter {
    @TypeConverter
    fun fromRewardType(rewardType: RewardType): Int {
        return when (rewardType) {
            RewardType.HINTS -> 0
            RewardType.XP_BOOST -> 1
            RewardType.BADGE -> 2
        }
    }

    @TypeConverter
    fun toRewardType(value: Int): RewardType {
        return when (value) {
            0 -> RewardType.HINTS
            1 -> RewardType.XP_BOOST
            2 -> RewardType.BADGE
            3 -> RewardType.BADGE // Legacy: THEME_UNLOCK -> BADGE migration
            else -> RewardType.HINTS
        }
    }
}
