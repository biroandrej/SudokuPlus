package sk.awisoft.sudokuplus.ai

import kotlinx.serialization.Serializable

/**
 * Configuration for the AI model used to generate hints.
 * This configuration is fetched from Firebase Remote Config.
 */
@Serializable
data class AIModelConfig(
    val modelName: String = DEFAULT_MODEL_NAME,
    val maxOutputTokens: Int = DEFAULT_MAX_OUTPUT_TOKENS,
    val temperature: Float = DEFAULT_TEMPERATURE
) {
    companion object {
        const val DEFAULT_MODEL_NAME = "gemini-2.0-flash"
        const val DEFAULT_MAX_OUTPUT_TOKENS = 500
        const val DEFAULT_TEMPERATURE = 0.3f

        const val REMOTE_CONFIG_KEY = "ai_model_config"
    }
}
