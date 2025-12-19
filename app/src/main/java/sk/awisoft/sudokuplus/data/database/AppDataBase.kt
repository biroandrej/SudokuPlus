package sk.awisoft.sudokuplus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import sk.awisoft.sudokuplus.data.database.converters.DurationConverter
import sk.awisoft.sudokuplus.data.database.converters.GameDifficultyConverter
import sk.awisoft.sudokuplus.data.database.converters.GameTypeConverter
import sk.awisoft.sudokuplus.data.database.converters.LocalDateConverter
import sk.awisoft.sudokuplus.data.database.converters.ZonedDateTimeConverter
import sk.awisoft.sudokuplus.data.database.dao.BoardDao
import sk.awisoft.sudokuplus.data.database.dao.DailyChallengeDao
import sk.awisoft.sudokuplus.data.database.dao.DatabaseDao
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.dao.SavedGameDao
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard

@Database(
    entities = [Record::class, SudokuBoard::class, SavedGame::class, Folder::class, DailyChallenge::class],
    version = 2
)
@TypeConverters(
    DurationConverter::class,
    ZonedDateTimeConverter::class,
    GameDifficultyConverter::class,
    GameTypeConverter::class,
    LocalDateConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun boardDao(): BoardDao
    abstract fun savedGameDao(): SavedGameDao
    abstract fun folderDao(): FolderDao
    abstract fun databaseDao(): DatabaseDao
    abstract fun dailyChallengeDao(): DailyChallengeDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS daily_challenge (
                        date INTEGER PRIMARY KEY NOT NULL,
                        difficulty INTEGER NOT NULL,
                        game_type INTEGER NOT NULL,
                        seed INTEGER NOT NULL,
                        initial_board TEXT NOT NULL,
                        solved_board TEXT NOT NULL,
                        current_board TEXT,
                        notes TEXT,
                        completed_at INTEGER,
                        completion_time INTEGER,
                        mistakes INTEGER NOT NULL DEFAULT 0,
                        hints_used INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
            }
        }

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "main_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }

            return INSTANCE as AppDatabase
        }
    }
}