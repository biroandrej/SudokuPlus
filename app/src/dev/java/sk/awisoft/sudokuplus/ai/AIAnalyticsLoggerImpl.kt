package sk.awisoft.sudokuplus.ai

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType

/**
 * Dev implementation that logs to Logcat for debugging.
 */
@Singleton
class AIAnalyticsLoggerImpl @Inject constructor(
    @ApplicationContext context: Context // Unused in dev, but matches prod signature
) : AIAnalyticsLogger {

    override fun logHintRequested(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        isPremium: Boolean
    ) {
        Log.d(TAG, "AI Hint Requested: game=$gameType, difficulty=$difficulty, " +
            "lang=$languageTag, premium=$isPremium")
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
        Log.d(TAG, "AI Hint Success: technique=$technique, latency=${latencyMs}ms, " +
            "model=$modelName, tokens=${promptTokens ?: 0}+${completionTokens ?: 0}")
    }

    override fun logHintError(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        errorType: AIErrorType,
        errorMessage: String,
        latencyMs: Long
    ) {
        Log.e(TAG, "AI Hint Error: type=$errorType, message=$errorMessage, latency=${latencyMs}ms")
    }

    override fun logHintApplied(
        technique: String,
        gameType: GameType,
        difficulty: GameDifficulty
    ) {
        Log.d(TAG, "AI Hint Applied: technique=$technique")
    }

    override fun logHintDismissed(
        technique: String,
        gameType: GameType,
        difficulty: GameDifficulty
    ) {
        Log.d(TAG, "AI Hint Dismissed: technique=$technique")
    }

    override fun logHintDialogShown() {
        Log.d(TAG, "AI Hint Dialog Shown")
    }

    override fun setUserPremiumStatus(isPremium: Boolean) {
        Log.d(TAG, "User Premium Status: $isPremium")
    }

    companion object {
        private const val TAG = "AIAnalytics"
    }
}
