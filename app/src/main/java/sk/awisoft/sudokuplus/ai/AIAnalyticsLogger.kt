package sk.awisoft.sudokuplus.ai

import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType

/**
 * Analytics logger for AI hint feature.
 * Tracks requests, responses, errors, and user interactions.
 */
interface AIAnalyticsLogger {
    /**
     * Log when an AI hint request is started.
     */
    fun logHintRequested(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        isPremium: Boolean
    )

    /**
     * Log a successful AI hint response.
     */
    fun logHintSuccess(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        technique: String,
        latencyMs: Long,
        modelName: String,
        promptTokens: Int?,
        completionTokens: Int?
    )

    /**
     * Log an AI hint error.
     */
    fun logHintError(
        gameType: GameType,
        difficulty: GameDifficulty,
        languageTag: String,
        errorType: AIErrorType,
        errorMessage: String,
        latencyMs: Long
    )

    /**
     * Log when user applies the AI hint to the board.
     */
    fun logHintApplied(technique: String, gameType: GameType, difficulty: GameDifficulty)

    /**
     * Log when user dismisses the AI hint without applying.
     */
    fun logHintDismissed(technique: String, gameType: GameType, difficulty: GameDifficulty)

    /**
     * Log when user views the AI hint dialog (for engagement tracking).
     */
    fun logHintDialogShown()

    /**
     * Set user properties for segmentation.
     */
    fun setUserPremiumStatus(isPremium: Boolean)
}

/**
 * Categorized error types for better monitoring and alerting.
 */
enum class AIErrorType {
    NETWORK_ERROR,
    TIMEOUT,
    RATE_LIMITED,
    INVALID_RESPONSE,
    PARSE_ERROR,
    MODEL_ERROR,
    UNKNOWN
}
