package sk.awisoft.sudokuplus.di

import android.app.Application
import android.content.Context
import sk.awisoft.sudokuplus.core.achievement.AchievementEngine
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.data.database.AppDatabase
import sk.awisoft.sudokuplus.data.database.dao.BoardDao
import sk.awisoft.sudokuplus.data.database.dao.DailyChallengeDao
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.dao.SavedGameDao
import sk.awisoft.sudokuplus.data.database.dao.UserAchievementDao
import sk.awisoft.sudokuplus.data.database.repository.AchievementRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.BoardRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.DailyChallengeRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.DatabaseRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.FolderRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.RecordRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.SavedGameRepositoryImpl
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.domain.repository.AchievementRepository
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.DatabaseRepository
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import sk.awisoft.sudokuplus.domain.repository.RecordRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabaseRepository(appDatabase: AppDatabase): DatabaseRepository
        = DatabaseRepositoryImpl(appDatabase)

    @Provides
    @Singleton
    fun provideFolderRepository(folderDao: FolderDao): FolderRepository
        = FolderRepositoryImpl(folderDao)

    @Provides
    @Singleton
    fun provideFolderDao(appDatabase: AppDatabase): FolderDao = appDatabase.folderDao()

    @Singleton
    @Provides
    fun provideRecordRepository(recordDao: RecordDao): RecordRepository =
        RecordRepositoryImpl(recordDao)

    @Singleton
    @Provides
    fun provideRecordDao(appDatabase: AppDatabase): RecordDao = appDatabase.recordDao()

    @Singleton
    @Provides
    fun provideBoardRepository(boardDao: BoardDao): BoardRepository = BoardRepositoryImpl(boardDao)

    @Singleton
    @Provides
    fun provideBoardDao(appDatabase: AppDatabase): BoardDao = appDatabase.boardDao()


    @Singleton
    @Provides
    fun provideSavedGameRepository(savedGameDao: SavedGameDao): SavedGameRepository =
        SavedGameRepositoryImpl(savedGameDao)

    @Singleton
    @Provides
    fun provideSavedGameDao(appDatabase: AppDatabase): SavedGameDao = appDatabase.savedGameDao()


    @Provides
    @Singleton
    fun provideAppSettingsManager(@ApplicationContext context: Context) =
        AppSettingsManager(context)

    @Provides
    @Singleton
    fun provideThemeSettingsManager(@ApplicationContext context: Context) =
        ThemeSettingsManager(context)

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase = AppDatabase.Companion.getInstance(context = app)

    @Singleton
    @Provides
    fun provideDailyChallengeDao(appDatabase: AppDatabase): DailyChallengeDao =
        appDatabase.dailyChallengeDao()

    @Singleton
    @Provides
    fun provideDailyChallengeRepository(dao: DailyChallengeDao): DailyChallengeRepository =
        DailyChallengeRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideDailyChallengeManager(repository: DailyChallengeRepository): DailyChallengeManager =
        DailyChallengeManager(repository)

    @Singleton
    @Provides
    fun provideUserAchievementDao(appDatabase: AppDatabase): UserAchievementDao =
        appDatabase.userAchievementDao()

    @Singleton
    @Provides
    fun provideAchievementRepository(dao: UserAchievementDao): AchievementRepository =
        AchievementRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideAchievementEngine(
        achievementRepository: AchievementRepository,
        savedGameRepository: SavedGameRepository,
        recordRepository: RecordRepository,
        dailyChallengeRepository: DailyChallengeRepository,
        dailyChallengeManager: DailyChallengeManager
    ): AchievementEngine = AchievementEngine(
        achievementRepository,
        savedGameRepository,
        recordRepository,
        dailyChallengeRepository,
        dailyChallengeManager
    )
}
