package sk.awisoft.sudokuplus.core.startup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import sk.awisoft.sudokuplus.ai.RemoteConfigProvider

/**
 * One-time worker that fetches and activates Remote Config values.
 * This runs in the background during app startup to ensure the latest
 * AI model configuration is available without blocking the main thread.
 *
 * Uses REPLACE policy so config is fetched on every app launch.
 * Firebase SDK's minimumFetchIntervalInSeconds (1 hour) throttles actual
 * network requests, so this is efficient while ensuring fresh config.
 */
@HiltWorker
class RemoteConfigFetchWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteConfigProvider: RemoteConfigProvider
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            remoteConfigProvider.fetchAndActivate()
            Result.success()
        } catch (e: Exception) {
            // Retry on failure, but don't block app startup
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        private const val WORK_NAME = "remote_config_fetch"
        private const val MAX_RETRIES = 3

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<RemoteConfigFetchWorker>()
                .build()

            // REPLACE ensures config is fetched on every app launch
            // Firebase SDK throttles actual network requests via minimumFetchInterval
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }
}
