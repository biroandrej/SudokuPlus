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
import sk.awisoft.sudokuplus.core.DailyChallengeManager
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.data.datastore.NotificationSettingsManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val notificationSettingsManager: NotificationSettingsManager,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val dailyChallengeManager: DailyChallengeManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val isEnabled = notificationSettingsManager.streakReminderEnabled.first()

        if (!isEnabled) {
            return Result.success()
        }

        // Check if user has completed today's daily challenge
        val todayChallenge = dailyChallengeRepository.get(LocalDate.now())
        val hasPlayedToday = todayChallenge?.completedAt != null

        if (!hasPlayedToday) {
            // Get current streak to show in notification
            val completedChallenges = dailyChallengeRepository.getCompleted().first()
            val currentStreak = dailyChallengeManager.calculateCurrentStreak(
                completedChallenges.map { it.date }
            )

            notificationHelper.showStreakReminderNotification(currentStreak)
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "streak_reminder_notification"

        fun schedule(context: Context, hour: Int = 20, minute: Int = 0) {
            val now = LocalDateTime.now()
            var scheduledTime = now.toLocalDate().atTime(LocalTime.of(hour, minute))

            // If the time has already passed today, schedule for tomorrow
            if (now.isAfter(scheduledTime)) {
                scheduledTime = scheduledTime.plusDays(1)
            }

            val initialDelay = Duration.between(now, scheduledTime).toMillis()

            val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
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
