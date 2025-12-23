package sk.awisoft.sudokuplus.data.database.converters

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Converts LocalDate to Long (epoch days) and back
 */
class LocalDateConverter {
    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? = date?.toEpochDay()
}
