package sk.awisoft.sudokuplus.core.notification

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltWorker
class DailyChallengeNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val notificationSettingsManager: NotificationSettingsManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val isEnabled = notificationSettingsManager.dailyChallengeNotificationEnabled.first()

        if (isEnabled) {
            notificationHelper.showDailyChallengeNotification()
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "daily_challenge_notification"

        fun schedule(context: Context, hour: Int = 8, minute: Int = 0) {
            val now = LocalDateTime.now()
            var scheduledTime = now.toLocalDate().atTime(LocalTime.of(hour, minute))

            // If the time has already passed today, schedule for tomorrow
            if (now.isAfter(scheduledTime)) {
                scheduledTime = scheduledTime.plusDays(1)
            }

            val initialDelay = Duration.between(now, scheduledTime).toMillis()

            val workRequest = PeriodicWorkRequestBuilder<DailyChallengeNotificationWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
