package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.data.backup.serializer.DurationLongSerializer
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

@Serializable
@Entity(
    tableName = "record",
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = SudokuBoard::class,
            parentColumns = arrayOf("uid"),
            childColumns = arrayOf("board_uid")
        )
    ]
)
data class Record(
    @PrimaryKey @ColumnInfo(name = "board_uid") val board_uid: Long,
    @ColumnInfo(name = "type") val type: GameType,
    @ColumnInfo(name = "difficulty") val difficulty: GameDifficulty,
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    @ColumnInfo(name = "date") val date: ZonedDateTime,
    @Serializable(with = DurationLongSerializer::class)
    @ColumnInfo(name = "time") val time: Duration
)
