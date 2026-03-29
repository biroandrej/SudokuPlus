package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasonal_events")
data class SeasonalEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "event_type")
    val eventType: String,
    @ColumnInfo(name = "start_date")
    val startDate: Long,
    @ColumnInfo(name = "end_date")
    val endDate: Long,
    @ColumnInfo(name = "theme_primary_color")
    val themePrimaryColor: Long,
    @ColumnInfo(name = "theme_secondary_color")
    val themeSecondaryColor: Long,
    @ColumnInfo(name = "theme_background_color")
    val themeBackgroundColor: Long,
    @ColumnInfo(name = "theme_accent_color")
    val themeAccentColor: Long,
    @ColumnInfo(name = "challenges_json")
    val challengesJson: String,
    @ColumnInfo(name = "rewards_json")
    val rewardsJson: String,
    @ColumnInfo(name = "badge_id")
    val badgeId: String,
    @ColumnInfo(name = "synced_at")
    val syncedAt: Long
)
