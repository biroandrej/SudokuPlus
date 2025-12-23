package sk.awisoft.sudokuplus.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime
import kotlinx.serialization.Serializable
import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer

@Serializable
@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true) val uid: Long,
    @ColumnInfo(name = "name") val name: String,
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    @ColumnInfo(name = "date_created") val createdAt: ZonedDateTime
)
