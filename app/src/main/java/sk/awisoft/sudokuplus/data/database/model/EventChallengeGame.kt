package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_challenge_games")
data class EventChallengeGame(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "event_id")
    val eventId: String,
    @ColumnInfo(name = "challenge_day")
    val challengeDay: Int,
    @ColumnInfo(name = "board_uid")
    val boardUid: Long,
    @ColumnInfo(name = "completed")
    val completed: Boolean = false
)
