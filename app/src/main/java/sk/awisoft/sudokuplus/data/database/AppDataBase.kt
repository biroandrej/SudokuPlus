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
import sk.awisoft.sudokuplus.data.database.converters.RewardTypeConverter
import sk.awisoft.sudokuplus.data.database.converters.ZonedDateTimeConverter
import sk.awisoft.sudokuplus.data.database.dao.BoardDao
import sk.awisoft.sudokuplus.data.database.dao.DailyChallengeDao
import sk.awisoft.sudokuplus.data.database.dao.DatabaseDao
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.dao.LoginRewardDao
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.dao.SavedGameDao
import sk.awisoft.sudokuplus.data.database.dao.UserAchievementDao
import sk.awisoft.sudokuplus.data.database.dao.RewardBadgeDao
import sk.awisoft.sudokuplus.data.database.dao.UserProgressDao
import sk.awisoft.sudokuplus.data.database.model.ClaimedReward
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.LoginRewardStatus
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.data.database.model.UserAchievement
import sk.awisoft.sudokuplus.data.database.model.RewardBadge
import sk.awisoft.sudokuplus.data.database.model.UserProgress

@Database(
    entities = [Record::class, SudokuBoard::class, SavedGame::class, Folder::class, DailyChallenge::class, UserAchievement::class, UserProgress::class, LoginRewardStatus::class, ClaimedReward::class, RewardBadge::class],
    version = 6
)
@TypeConverters(
    DurationConverter::class,
    ZonedDateTimeConverter::class,
    GameDifficultyConverter::class,
    GameTypeConverter::class,
    LocalDateConverter::class,
    RewardTypeConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun boardDao(): BoardDao
    abstract fun savedGameDao(): SavedGameDao
    abstract fun folderDao(): FolderDao
    abstract fun databaseDao(): DatabaseDao
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun userAchievementDao(): UserAchievementDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun loginRewardDao(): LoginRewardDao
    abstract fun rewardBadgeDao(): RewardBadgeDao

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

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_achievement (
                        achievement_id TEXT PRIMARY KEY NOT NULL,
                        progress INTEGER NOT NULL DEFAULT 0,
                        unlocked_at INTEGER
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS user_progress (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        total_xp INTEGER NOT NULL DEFAULT 0,
                        level INTEGER NOT NULL DEFAULT 1,
                        current_level_xp INTEGER NOT NULL DEFAULT 0,
                        xp_to_next_level INTEGER NOT NULL DEFAULT 100,
                        games_for_xp INTEGER NOT NULL DEFAULT 0,
                        last_xp_earned_at INTEGER,
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS login_reward_status (
                        id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                        current_day INTEGER NOT NULL DEFAULT 1,
                        last_claim_date INTEGER,
                        cycle_start_date INTEGER NOT NULL,
                        total_days_claimed INTEGER NOT NULL DEFAULT 0,
                        bonus_hints INTEGER NOT NULL DEFAULT 0,
                        xp_boost_games_remaining INTEGER NOT NULL DEFAULT 0
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS claimed_rewards (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        day INTEGER NOT NULL,
                        reward_type INTEGER NOT NULL,
                        amount INTEGER NOT NULL,
                        claimed_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS reward_badges (
                        badge_id TEXT PRIMARY KEY NOT NULL,
                        earned_at INTEGER NOT NULL,
                        cycle_number INTEGER NOT NULL DEFAULT 1
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
            }

            return INSTANCE as AppDatabase
        }
    }
}