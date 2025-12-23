package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

/**
 * Represents an earned badge from the reward calendar
 */
@Serializable
@Entity(tableName = "reward_badges")
data class RewardBadge(
    @PrimaryKey
    @ColumnInfo(name = "badge_id")
    val badgeId: String,
    @ColumnInfo(name = "earned_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val earnedAt: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "cycle_number")
    val cycleNumber: Int = 1
)
