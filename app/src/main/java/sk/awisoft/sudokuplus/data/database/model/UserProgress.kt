package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

@Serializable
@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1, // Singleton row - only one user progress record
    @ColumnInfo(name = "total_xp")
    val totalXP: Long = 0,
    @ColumnInfo(name = "level")
    val level: Int = 1,
    @ColumnInfo(name = "current_level_xp")
    val currentLevelXP: Long = 0,
    @ColumnInfo(name = "xp_to_next_level")
    val xpToNextLevel: Long = 100,
    @ColumnInfo(name = "games_for_xp")
    val gamesForXP: Int = 0, // Total games that earned XP
    @ColumnInfo(name = "last_xp_earned_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val lastXPEarnedAt: ZonedDateTime? = null,
    @ColumnInfo(name = "created_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val createdAt: ZonedDateTime = ZonedDateTime.now()
) {
    val progressPercent: Float
        get() = if (xpToNextLevel > 0) currentLevelXP.toFloat() / xpToNextLevel.toFloat() else 0f
}
