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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sk.awisoft.sudokuplus.ads.AdsInitializer

/**
 * One-time worker that initializes Mobile Ads SDK in the background.
 * This defers ads initialization from blocking app startup while ensuring
 * ads are ready before they're needed.
 *
 * Uses REPLACE policy so initialization is retried on subsequent app launches
 * if it previously failed (e.g., missing Play Services, network issues).
 */
@HiltWorker
class AdsInitWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.Main) {
            try {
                AdsInitializer.initialize(applicationContext)
                Result.success()
            } catch (e: Exception) {
                // Retry on transient failures (network, Play Services not ready)
                if (runAttemptCount < MAX_RETRIES) {
                    Result.retry()
                } else {
                    Result.failure()
                }
            }
        }
    }

    companion object {
        private const val WORK_NAME = "ads_init"
        private const val MAX_RETRIES = 3

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<AdsInitWorker>()
                .build()

            // REPLACE ensures failed work is retried on next app launch
            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    request
                )
        }
    }
}
