package sk.awisoft.sudokuplus

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import sk.awisoft.sudokuplus.core.notification.NotificationInitializer
import sk.awisoft.sudokuplus.core.seasonal.SeasonalEventSyncWorker
import sk.awisoft.sudokuplus.core.startup.AdsInitWorker
import sk.awisoft.sudokuplus.core.startup.RemoteConfigFetchWorker

@HiltAndroidApp
class SudokuPlusApp : Application(), Configuration.Provider {
    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    @Inject
    lateinit var notificationInitializer: NotificationInitializer

    override val workManagerConfiguration: Configuration
        get() =
            Configuration.Builder()
                .setWorkerFactory(hiltWorkerFactory)
                .build()

    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.init(this)
        initCrashlytics()
        WorkManager.initialize(this, workManagerConfiguration)

        // Schedule background initialization tasks
        scheduleStartupWorkers()
    }

    private fun initCrashlytics() {
        CrashlyticsInitializer.init()
    }

    /**
     * Schedules one-time workers for initialization tasks that don't need
     * to block app startup. This improves cold start time.
     */
    private fun scheduleStartupWorkers() {
        // Fetch latest Remote Config (AI model settings)
        RemoteConfigFetchWorker.enqueue(this)

        // Initialize Mobile Ads SDK in background
        AdsInitWorker.enqueue(this)

        // Sync seasonal events from Firestore periodically
        SeasonalEventSyncWorker.enqueue(this)

        // Schedule notification workers based on user settings
        notificationInitializer.scheduleWorkersIfNeeded(this)
    }
}
