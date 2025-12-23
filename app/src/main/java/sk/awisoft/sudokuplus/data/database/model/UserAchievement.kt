package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

@Serializable
@Entity(tableName = "user_achievement")
data class UserAchievement(
    @PrimaryKey
    @ColumnInfo(name = "achievement_id")
    val achievementId: String,
    @ColumnInfo(name = "progress")
    val progress: Int = 0,
    @ColumnInfo(name = "unlocked_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val unlockedAt: ZonedDateTime? = null
) {
    val isUnlocked: Boolean
        get() = unlockedAt != null
}
