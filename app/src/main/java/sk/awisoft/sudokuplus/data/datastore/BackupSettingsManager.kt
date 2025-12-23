package sk.awisoft.sudokuplus.data.datastore

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.map
import sk.awisoft.sudokuplus.core.PreferencesConstants

@Singleton
class BackupSettingsManager
@Inject
constructor(
    settingsDataStore: SettingsDataStore
) {
    private val dataStore = settingsDataStore.dataStore

    private val backupUriKey = stringPreferencesKey("backup_persistent_uri")
    private val autoBackupIntervalKey = longPreferencesKey("auto_backup_interval")
    private val autoBackupsNumberKey = intPreferencesKey("auto_backups_max_number")
    private val lastBackupDateKey = longPreferencesKey("last_backup_date")
    private val lastBackupFailureKey = stringPreferencesKey("last_backup_failure")
    private val lastBackupFailureDateKey = longPreferencesKey("last_backup_failure_date")

    // Backup URI
    val backupUri = dataStore.data.map { prefs -> prefs[backupUriKey] ?: "" }

    suspend fun setBackupUri(uri: String) {
        dataStore.edit { settings ->
            settings[backupUriKey] = uri
        }
    }

    // Auto backup interval
    val autoBackupInterval =
        dataStore.data.map { prefs ->
            prefs[autoBackupIntervalKey] ?: PreferencesConstants.DEFAULT_AUTOBACKUP_INTERVAL
        }

    suspend fun setAutoBackupInterval(hours: Long) {
        dataStore.edit { settings ->
            settings[autoBackupIntervalKey] = hours
        }
    }

    // Max auto backups
    val autoBackupsNumber =
        dataStore.data.map { prefs ->
            prefs[autoBackupsNumberKey] ?: PreferencesConstants.DEFAULT_AUTO_BACKUPS_NUMBER
        }

    suspend fun setAutoBackupsNumber(value: Int) {
        dataStore.edit { settings ->
            settings[autoBackupsNumberKey] = value
        }
    }

    // Last backup date
    val lastBackupDate =
        dataStore.data.map { prefs ->
            val date = prefs[lastBackupDateKey]
            if (date != null) {
                ZonedDateTime.ofInstant(Instant.ofEpochSecond(date), ZoneId.systemDefault())
            } else {
                null
            }
        }

    suspend fun setLastBackupDate(date: ZonedDateTime) {
        dataStore.edit { settings ->
            settings[lastBackupDateKey] = date.toInstant().epochSecond
        }
    }

    // Last backup failure
    val lastBackupFailure =
        dataStore.data.map { prefs ->
            val reason = prefs[lastBackupFailureKey]
            val date = prefs[lastBackupFailureDateKey]
            if (reason != null && date != null) {
                BackupFailure(
                    reason = reason,
                    date = ZonedDateTime.ofInstant(
                        Instant.ofEpochSecond(date),
                        ZoneId.systemDefault()
                    )
                )
            } else {
                null
            }
        }

    suspend fun setLastBackupFailure(reason: String) {
        dataStore.edit { settings ->
            settings[lastBackupFailureKey] = reason
            settings[lastBackupFailureDateKey] = ZonedDateTime.now().toInstant().epochSecond
        }
    }

    suspend fun clearLastBackupFailure() {
        dataStore.edit { settings ->
            settings.remove(lastBackupFailureKey)
            settings.remove(lastBackupFailureDateKey)
        }
    }
}

data class BackupFailure(
    val reason: String,
    val date: ZonedDateTime
)
