package sk.awisoft.sudokuplus.core.whatsnew

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager

@Singleton
class WhatsNewManager @Inject constructor(
    private val appSettingsManager: AppSettingsManager
) {
    suspend fun shouldShow(): Boolean {
        val lastSeen = appSettingsManager.lastSeenWhatsNewVersion.first()
        val latest = WhatsNewRepository.getLatestEntry() ?: return false
        return latest.versionCode > lastSeen
    }

    suspend fun markSeen() {
        val latest = WhatsNewRepository.getLatestEntry() ?: return
        appSettingsManager.setLastSeenWhatsNewVersion(latest.versionCode)
    }

    fun getLatestEntry(): WhatsNewEntry? = WhatsNewRepository.getLatestEntry()

    fun getAllEntries(): List<WhatsNewEntry> = WhatsNewRepository.allEntries
}
