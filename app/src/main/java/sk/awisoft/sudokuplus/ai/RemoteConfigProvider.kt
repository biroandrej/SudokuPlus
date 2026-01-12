package sk.awisoft.sudokuplus.ai

/**
 * Provider for fetching remote configuration values.
 * Has separate implementations for dev (defaults) and prod (Firebase Remote Config).
 */
interface RemoteConfigProvider {

    /**
     * Fetches the AI model configuration.
     * Returns default values if remote config is unavailable.
     */
    suspend fun getAIModelConfig(): AIModelConfig

    /**
     * Fetches and activates the latest remote config values.
     * Should be called on app startup.
     */
    suspend fun fetchAndActivate(): Boolean
}
