package sk.awisoft.sudokuplus.data.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.awisoft.sudokuplus.data.database.converters.DurationConverter
import sk.awisoft.sudokuplus.data.database.converters.GameDifficultyConverter
import sk.awisoft.sudokuplus.data.database.converters.GameTypeConverter
import sk.awisoft.sudokuplus.data.database.converters.ZonedDateTimeConverter
import sk.awisoft.sudokuplus.data.database.dao.BoardDao
import sk.awisoft.sudokuplus.data.database.dao.DatabaseDao
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.dao.SavedGameDao
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard

@Database(
    entities = [Record::class, SudokuBoard::class, SavedGame::class, Folder::class],
    version = 1
)
@TypeConverters(
    DurationConverter::class,
    ZonedDateTimeConverter::class,
    GameDifficultyConverter::class,
    GameTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun boardDao(): BoardDao
    abstract fun savedGameDao(): SavedGameDao

    abstract fun folderDao(): FolderDao

    abstract  fun databaseDao(): DatabaseDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "main_database"
                ).build()
            }

            return INSTANCE as AppDatabase
        }
    }
}