package sk.awisoft.sudokuplus

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import sk.awisoft.sudokuplus.core.notification.DailyChallengeNotificationWorker
import sk.awisoft.sudokuplus.core.notification.NotificationHelper
import sk.awisoft.sudokuplus.core.notification.StreakReminderWorker
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import sk.awisoft.sudokuplus.playgames.PlayGamesManagerImpl

@HiltAndroidApp
class SudokuPlusApp : Application(), Configuration.Provider {
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationSettingsManager: NotificationSettingsManager

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder()
                .setWorkerFactory(hiltWorkerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
        initPlayGames()
        WorkManager.initialize(this, workManagerConfiguration)
        scheduleNotificationWorkersIfEnabled()
    }

    private fun initPlayGames() {
        PlayGamesManagerImpl.initialize(this)
    }

    private fun initCrashlytics() {
        CrashlyticsInitializer.init()
    }

    private fun scheduleNotificationWorkersIfEnabled() {
        applicationScope.launch {
            // Only schedule if we have notification permission
            if (!notificationHelper.hasNotificationPermission()) return@launch

            val dailyEnabled = notificationSettingsManager.dailyChallengeNotificationEnabled.first()
            val streakEnabled = notificationSettingsManager.streakReminderEnabled.first()

            if (dailyEnabled) {
                val hour = notificationSettingsManager.dailyChallengeNotificationHour.first()
                val minute = notificationSettingsManager.dailyChallengeNotificationMinute.first()
                DailyChallengeNotificationWorker.schedule(this@SudokuPlusApp, hour, minute)
            }

            if (streakEnabled) {
                val hour = notificationSettingsManager.streakReminderHour.first()
                val minute = notificationSettingsManager.streakReminderMinute.first()
                StreakReminderWorker.schedule(this@SudokuPlusApp, hour, minute)
            }
        }
    }
}
