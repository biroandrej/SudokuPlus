package sk.awisoft.sudokuplus.review

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

private val Context.reviewDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "review_settings"
)

object ReviewManager {
    private const val TAG = "ReviewManager"

    private val REVIEW_PROMPT_COUNT_KEY = intPreferencesKey("review_prompt_count")
    private val LAST_REVIEW_PROMPT_KEY = longPreferencesKey("last_review_prompt_timestamp")

    private val MILESTONE_GAMES = listOf(5, 15, 50)
    private const val MAX_PROMPTS = 3
    private const val COOLDOWN_DAYS = 30
    private const val COOLDOWN_MS = COOLDOWN_DAYS * 24 * 60 * 60 * 1000L

    suspend fun requestReviewIfEligible(activity: Activity, completedGames: Int) {
        try {
            val dataStore = activity.applicationContext.reviewDataStore
            val prefs = dataStore.data.first()
            val promptCount = prefs[REVIEW_PROMPT_COUNT_KEY] ?: 0
            val lastPromptTime = prefs[LAST_REVIEW_PROMPT_KEY] ?: 0L
            val currentTime = System.currentTimeMillis()

            if (!shouldShowReview(completedGames, promptCount, lastPromptTime, currentTime)) {
                return
            }

            val reviewManager = ReviewManagerFactory.create(activity)
            val reviewInfo = reviewManager.requestReviewFlow().await()

            reviewManager.launchReviewFlow(activity, reviewInfo).await()

            dataStore.edit { settings ->
                settings[REVIEW_PROMPT_COUNT_KEY] = promptCount + 1
                settings[LAST_REVIEW_PROMPT_KEY] = currentTime
            }

            Log.d(TAG, "Review flow completed. Prompt count: ${promptCount + 1}")
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting review", e)
        }
    }

    private fun shouldShowReview(
        completedGames: Int,
        promptCount: Int,
        lastPromptTime: Long,
        currentTime: Long
    ): Boolean {
        if (promptCount >= MAX_PROMPTS) {
            Log.d(TAG, "Max prompts reached: $promptCount")
            return false
        }

        if (lastPromptTime > 0 && currentTime - lastPromptTime < COOLDOWN_MS) {
            Log.d(TAG, "Cooldown not elapsed")
            return false
        }

        if (completedGames !in MILESTONE_GAMES) {
            return false
        }

        Log.d(TAG, "Review eligible: games=$completedGames, prompts=$promptCount")
        return true
    }
}
