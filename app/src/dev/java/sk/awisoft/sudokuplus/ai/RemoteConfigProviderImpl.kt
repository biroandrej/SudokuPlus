package sk.awisoft.sudokuplus.ai

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Dev implementation that returns default values.
 * Does not use Firebase Remote Config to allow testing without Firebase setup.
 */
@Singleton
class RemoteConfigProviderImpl @Inject constructor() : RemoteConfigProvider {

    override suspend fun getAIModelConfig(): AIModelConfig {
        return AIModelConfig()
    }

    override suspend fun fetchAndActivate(): Boolean {
        return true
    }
}
