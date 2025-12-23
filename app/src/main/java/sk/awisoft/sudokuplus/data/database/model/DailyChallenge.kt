package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.backup.serializer.DurationLongSerializer
import sk.awisoft.sudokuplus.data.backup.serializer.LocalDateLongSerializer
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

@Serializable
@Entity(tableName = "daily_challenge")
data class DailyChallenge(
    @PrimaryKey
    @ColumnInfo(name = "date")
    @Serializable(with = LocalDateLongSerializer::class)
    val date: LocalDate,
    @ColumnInfo(name = "difficulty")
    val difficulty: GameDifficulty,
    @ColumnInfo(name = "game_type")
    val gameType: GameType,
    @ColumnInfo(name = "seed")
    val seed: Long,
    @ColumnInfo(name = "initial_board")
    val initialBoard: String,
    @ColumnInfo(name = "solved_board")
    val solvedBoard: String,
    @ColumnInfo(name = "current_board")
    val currentBoard: String? = null,
    @ColumnInfo(name = "notes")
    val notes: String? = null,
    @ColumnInfo(name = "completed_at")
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val completedAt: ZonedDateTime? = null,
    @ColumnInfo(name = "completion_time")
    @Serializable(with = DurationLongSerializer::class)
    val completionTime: Duration? = null,
    @ColumnInfo(name = "mistakes")
    val mistakes: Int = 0,
    @ColumnInfo(name = "hints_used")
    val hintsUsed: Int = 0
)
