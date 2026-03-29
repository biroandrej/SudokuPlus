package sk.awisoft.sudokuplus.core.seasonal

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import sk.awisoft.sudokuplus.ai.RemoteConfigProvider
import sk.awisoft.sudokuplus.domain.repository.SeasonalEventRepository
import java.util.concurrent.TimeUnit

@HiltWorker
class SeasonalEventSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SeasonalEventRepository,
    private val remoteConfigProvider: RemoteConfigProvider
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!remoteConfigProvider.isSeasonalEventsEnabled()) {
            return Result.success()
        }

        return try {
            repository.syncEvents()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val WORK_NAME = "seasonal_event_sync"
        private const val MAX_RETRIES = 3
        private const val SYNC_INTERVAL_HOURS = 6L

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<SeasonalEventSyncWorker>(
                SYNC_INTERVAL_HOURS, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        }
    }
}
