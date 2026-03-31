package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "event_progress",
    foreignKeys = [
        ForeignKey(
            entity = SeasonalEventEntity::class,
            parentColumns = ["id"],
            childColumns = ["event_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("event_id")]
)
data class EventProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    val eventId: String,
    @ColumnInfo(name = "challenges_completed")
    val challengesCompleted: Int = 0,
    @ColumnInfo(name = "last_challenge_day")
    val lastChallengeDay: Int = 0,
    @ColumnInfo(name = "badge_earned")
    val badgeEarned: Boolean = false,
    @ColumnInfo(name = "badge_earned_at")
    val badgeEarnedAt: Long? = null,
    @ColumnInfo(name = "total_xp_earned")
    val totalXpEarned: Int = 0
)
