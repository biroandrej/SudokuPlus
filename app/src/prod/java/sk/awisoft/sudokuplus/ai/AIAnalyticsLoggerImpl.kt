package sk.awisoft.sudokuplus.ai

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType

@Singleton
class AIAnalyticsLoggerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AIAnalyticsLogger {
    private val analytics by lazy { FirebaseAnalytics.getInstance(context) }
    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun logHintRequested(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        isPremium: Boolean
    ) {
        analytics.logEvent(EVENT_HINT_REQUESTED, Bundle().apply {
            putString(PARAM_GAME_TYPE, gameType.name)
            putString(PARAM_DIFFICULTY, difficulty.name)
            putString(PARAM_LANGUAGE, languageTag)
            putBoolean(PARAM_IS_PREMIUM, isPremium)
        })

        // Set Crashlytics keys for context if errors occur
        crashlytics.setCustomKey(KEY_LAST_GAME_TYPE, gameType.name)
        crashlytics.setCustomKey(KEY_LAST_DIFFICULTY, difficulty.name)
        crashlytics.setCustomKey(KEY_LAST_LANGUAGE, languageTag)
    }

    override fun logHintSuccess(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        technique: String,
        latencyMs: Long,
        modelName: String,
        promptTokens: Int?,
        completionTokens: Int?
    ) {
        analytics.logEvent(EVENT_HINT_SUCCESS, Bundle().apply {
            putString(PARAM_GAME_TYPE, gameType.name)
            putString(PARAM_DIFFICULTY, difficulty.name)
            putString(PARAM_LANGUAGE, languageTag)
            putString(PARAM_TECHNIQUE, technique)
            putLong(PARAM_LATENCY_MS, latencyMs)
            putString(PARAM_MODEL_NAME, modelName)
            promptTokens?.let { putInt(PARAM_PROMPT_TOKENS, it) }
            completionTokens?.let { putInt(PARAM_COMPLETION_TOKENS, it) }
            val totalTokens = (promptTokens ?: 0) + (completionTokens ?: 0)
            if (totalTokens > 0) {
                putInt(PARAM_TOTAL_TOKENS, totalTokens)
            }
        })

        // Clear error state in Crashlytics
        crashlytics.setCustomKey(KEY_LAST_AI_ERROR, "none")
    }

    override fun logHintError(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        errorType: AIErrorType,
        errorMessage: String,
        latencyMs: Long
    ) {
        analytics.logEvent(EVENT_HINT_ERROR, Bundle().apply {
            putString(PARAM_GAME_TYPE, gameType.name)
            putString(PARAM_DIFFICULTY, difficulty.name)
            putString(PARAM_LANGUAGE, languageTag)
            putString(PARAM_ERROR_TYPE, errorType.name)
            putString(PARAM_ERROR_MESSAGE, errorMessage.take(100)) // Truncate long messages
            putLong(PARAM_LATENCY_MS, latencyMs)
        })

        // Set Crashlytics keys for debugging
        crashlytics.setCustomKey(KEY_LAST_AI_ERROR, errorType.name)
        crashlytics.setCustomKey(KEY_LAST_AI_ERROR_MSG, errorMessage.take(100))

        // Log non-fatal for rate limits and model errors (helps track frequency)
        if (errorType == AIErrorType.RATE_LIMITED || errorType == AIErrorType.MODEL_ERROR) {
            crashlytics.log("AI Hint Error: $errorType - $errorMessage")
        }
    }

    override fun logHintApplied(
        technique: String,
        gameType: GameType,
        difficulty: GameDifficulty
    ) {
        analytics.logEvent(EVENT_HINT_APPLIED, Bundle().apply {
            putString(PARAM_TECHNIQUE, technique)
            putString(PARAM_GAME_TYPE, gameType.name)
            putString(PARAM_DIFFICULTY, difficulty.name)
        })
    }

    override fun logHintDismissed(
        technique: String,
        gameType: GameType,
        difficulty: GameDifficulty
    ) {
        analytics.logEvent(EVENT_HINT_DISMISSED, Bundle().apply {
            putString(PARAM_TECHNIQUE, technique)
            putString(PARAM_GAME_TYPE, gameType.name)
            putString(PARAM_DIFFICULTY, difficulty.name)
        })
    }

    override fun logHintDialogShown() {
        analytics.logEvent(EVENT_HINT_DIALOG_SHOWN, null)
    }

    override fun setUserPremiumStatus(isPremium: Boolean) {
        analytics.setUserProperty(USER_PROP_PREMIUM, isPremium.toString())
        crashlytics.setCustomKey(KEY_IS_PREMIUM, isPremium)
    }

    companion object {
        // Event names
        private const val EVENT_HINT_REQUESTED = "ai_hint_requested"
        private const val EVENT_HINT_SUCCESS = "ai_hint_success"
        private const val EVENT_HINT_ERROR = "ai_hint_error"
        private const val EVENT_HINT_APPLIED = "ai_hint_applied"
        private const val EVENT_HINT_DISMISSED = "ai_hint_dismissed"
        private const val EVENT_HINT_DIALOG_SHOWN = "ai_hint_dialog_shown"

        // Event parameters
        private const val PARAM_GAME_TYPE = "game_type"
        private const val PARAM_DIFFICULTY = "difficulty"
        private const val PARAM_LANGUAGE = "language"
        private const val PARAM_IS_PREMIUM = "is_premium"
        private const val PARAM_TECHNIQUE = "technique"
        private const val PARAM_LATENCY_MS = "latency_ms"
        private const val PARAM_MODEL_NAME = "model_name"
        private const val PARAM_PROMPT_TOKENS = "prompt_tokens"
        private const val PARAM_COMPLETION_TOKENS = "completion_tokens"
        private const val PARAM_TOTAL_TOKENS = "total_tokens"
        private const val PARAM_ERROR_TYPE = "error_type"
        private const val PARAM_ERROR_MESSAGE = "error_message"

        // User properties
        private const val USER_PROP_PREMIUM = "is_premium_user"

        // Crashlytics custom keys
        private const val KEY_LAST_GAME_TYPE = "ai_last_game_type"
        private const val KEY_LAST_DIFFICULTY = "ai_last_difficulty"
        private const val KEY_LAST_LANGUAGE = "ai_last_language"
        private const val KEY_LAST_AI_ERROR = "ai_last_error"
        private const val KEY_LAST_AI_ERROR_MSG = "ai_last_error_msg"
        private const val KEY_IS_PREMIUM = "is_premium"
    }
}
