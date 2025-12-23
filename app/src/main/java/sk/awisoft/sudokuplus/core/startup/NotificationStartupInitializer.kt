package sk.awisoft.sudokuplus.core.startup

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import sk.awisoft.sudokuplus.core.notification.NotificationInitializer

/**
 * App Startup initializer that creates notification channels.
 * Note: Worker scheduling is done in SudokuPlusApp.onCreate() because
 * WorkManager with HiltWorkerFactory requires Hilt to be initialized first,
 * which happens after App Startup initializers run.
 */
class NotificationStartupInitializer : Initializer<Unit> {
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NotificationInitializerEntryPoint {
        fun notificationInitializer(): NotificationInitializer
    }

    override fun create(context: Context) {
        val entryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                NotificationInitializerEntryPoint::class.java
            )
        val notificationInitializer = entryPoint.notificationInitializer()

        // Only create channels here - worker scheduling is done in Application.onCreate()
        notificationInitializer.createChannels()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AdsStartupInitializer::class.java
    )
}
