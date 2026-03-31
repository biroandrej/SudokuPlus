package sk.awisoft.sudokuplus.data.database.converters

import androidx.room.TypeConverter
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty

class GameDifficultyConverter {
    @TypeConverter
    fun fromDifficulty(gameDifficulty: GameDifficulty): Int = gameDifficulty.ordinal

    @TypeConverter
    fun toDifficulty(value: Int): GameDifficulty =
        GameDifficulty.entries.getOrElse(value) { GameDifficulty.Unspecified }
}
