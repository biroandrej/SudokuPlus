package sk.awisoft.sudokuplus.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.core.achievement.AchievementEngine
import sk.awisoft.sudokuplus.core.reward.RewardCalendarManager
import sk.awisoft.sudokuplus.core.xp.XPEngine
import sk.awisoft.sudokuplus.data.database.AppDatabase
import sk.awisoft.sudokuplus.data.database.dao.BoardDao
import sk.awisoft.sudokuplus.data.database.dao.DailyChallengeDao
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.dao.LoginRewardDao
import sk.awisoft.sudokuplus.data.database.dao.RecordDao
import sk.awisoft.sudokuplus.data.database.dao.RewardBadgeDao
import sk.awisoft.sudokuplus.data.database.dao.SavedGameDao
import sk.awisoft.sudokuplus.data.database.dao.UserAchievementDao
import sk.awisoft.sudokuplus.data.database.dao.UserProgressDao
import sk.awisoft.sudokuplus.data.database.repository.AchievementRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.BoardRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.DailyChallengeRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.DatabaseRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.FolderRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.LoginRewardRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.RecordRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.SavedGameRepositoryImpl
import sk.awisoft.sudokuplus.data.database.repository.UserProgressRepositoryImpl
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.AssistanceSettingsManager
import sk.awisoft.sudokuplus.data.datastore.BackupSettingsManager
import sk.awisoft.sudokuplus.data.datastore.GameplaySettingsManager
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import sk.awisoft.sudokuplus.data.datastore.PlayGamesSettingsManager
import sk.awisoft.sudokuplus.data.datastore.SettingsDataStore
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.playgames.PlayGamesManager
import sk.awisoft.sudokuplus.playgames.PlayGamesManagerImpl
import sk.awisoft.sudokuplus.domain.repository.AchievementRepository
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.DatabaseRepository
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import sk.awisoft.sudokuplus.domain.repository.LoginRewardRepository
import sk.awisoft.sudokuplus.domain.repository.RecordRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository
import sk.awisoft.sudokuplus.domain.repository.UserProgressRepository

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDatabaseRepository(appDatabase: AppDatabase): DatabaseRepository =
        DatabaseRepositoryImpl(appDatabase)

    @Provides
    @Singleton
    fun provideFolderRepository(folderDao: FolderDao): FolderRepository =
        FolderRepositoryImpl(folderDao)

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
    fun provideSettingsDataStore(@ApplicationContext context: Context) = SettingsDataStore(context)

    @Provides
    @Singleton
    fun provideGameplaySettingsManager(settingsDataStore: SettingsDataStore) =
        GameplaySettingsManager(settingsDataStore)

    @Provides
    @Singleton
    fun provideAssistanceSettingsManager(settingsDataStore: SettingsDataStore) =
        AssistanceSettingsManager(settingsDataStore)

    @Provides
    @Singleton
    fun provideBackupSettingsManager(settingsDataStore: SettingsDataStore) =
        BackupSettingsManager(settingsDataStore)

    @Provides
    @Singleton
    fun provideNotificationSettingsManager(settingsDataStore: SettingsDataStore) =
        NotificationSettingsManager(settingsDataStore)

    @Provides
    @Singleton
    fun provideAppSettingsManager(
        settingsDataStore: SettingsDataStore,
        gameplay: GameplaySettingsManager,
        assistance: AssistanceSettingsManager,
        backup: BackupSettingsManager,
        notification: NotificationSettingsManager
    ) = AppSettingsManager(settingsDataStore, gameplay, assistance, backup, notification)

    @Provides
    @Singleton
    fun provideThemeSettingsManager(@ApplicationContext context: Context) =
        ThemeSettingsManager(context)

    @Singleton
    @Provides
    fun provideAppDatabase(app: Application): AppDatabase =
        AppDatabase.Companion.getInstance(context = app)

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

    @Singleton
    @Provides
    fun provideUserProgressDao(appDatabase: AppDatabase): UserProgressDao =
        appDatabase.userProgressDao()

    @Singleton
    @Provides
    fun provideUserProgressRepository(dao: UserProgressDao): UserProgressRepository =
        UserProgressRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideXPEngine(
        userProgressRepository: UserProgressRepository,
        dailyChallengeRepository: DailyChallengeRepository,
        dailyChallengeManager: DailyChallengeManager,
        rewardCalendarManager: RewardCalendarManager
    ): XPEngine = XPEngine(
        userProgressRepository,
        dailyChallengeRepository,
        dailyChallengeManager,
        rewardCalendarManager
    )

    @Singleton
    @Provides
    fun provideLoginRewardDao(appDatabase: AppDatabase): LoginRewardDao =
        appDatabase.loginRewardDao()

    @Singleton
    @Provides
    fun provideLoginRewardRepository(dao: LoginRewardDao): LoginRewardRepository =
        LoginRewardRepositoryImpl(dao)

    @Singleton
    @Provides
    fun provideRewardBadgeDao(appDatabase: AppDatabase): RewardBadgeDao =
        appDatabase.rewardBadgeDao()

    @Singleton
    @Provides
    fun provideRewardCalendarManager(
        repository: LoginRewardRepository,
        badgeDao: RewardBadgeDao
    ): RewardCalendarManager = RewardCalendarManager(repository, badgeDao)

    @Provides
    @Singleton
    fun providePlayGamesSettingsManager(settingsDataStore: SettingsDataStore) =
        PlayGamesSettingsManager(settingsDataStore)

    @Provides
    @Singleton
    fun providePlayGamesManager(): PlayGamesManager = PlayGamesManagerImpl
}
