package sk.awisoft.sudokuplus.ai

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json

@Singleton
class RemoteConfigProviderImpl @Inject constructor() : RemoteConfigProvider {

    private val json = Json { ignoreUnknownKeys = true }

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 3600 // 1 hour
                }
            )
            setDefaultsAsync(
                mapOf(
                    AIModelConfig.REMOTE_CONFIG_KEY to DEFAULT_AI_MODEL_CONFIG_JSON
                )
            )
        }
    }

    override suspend fun getAIModelConfig(): AIModelConfig {
        return try {
            val configJson = remoteConfig.getString(AIModelConfig.REMOTE_CONFIG_KEY)
            if (configJson.isNotBlank()) {
                json.decodeFromString<AIModelConfig>(configJson)
            } else {
                AIModelConfig()
            }
        } catch (e: Exception) {
            AIModelConfig()
        }
    }

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        // Note: This JSON uses the constants from AIModelConfig for consistency
        private val DEFAULT_AI_MODEL_CONFIG_JSON = """
            {
                "modelName": "gemini-2.5-flash",
                "maxOutputTokens": 500,
                "temperature": 0.3
            }
        """.trimIndent()
    }
}
