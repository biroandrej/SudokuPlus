package sk.awisoft.sudokuplus.ui.backup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import sk.awisoft.sudokuplus.BuildConfig
import sk.awisoft.sudokuplus.data.backup.BackupData
import sk.awisoft.sudokuplus.data.backup.SettingsBackup
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.domain.repository.DailyChallengeRepository
import sk.awisoft.sudokuplus.domain.repository.DatabaseRepository
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import sk.awisoft.sudokuplus.domain.repository.RecordRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository
import sk.awisoft.sudokuplus.util.FlavorUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.OutputStream
import java.time.ZonedDateTime
import javax.inject.Inject


@HiltViewModel
class BackupScreenViewModel @Inject constructor(
    private val appSettingsManager: AppSettingsManager,
    private val themeSettingsManager: ThemeSettingsManager,
    private val boardRepository: BoardRepository,
    private val folderRepository: FolderRepository,
    private val recordRepository: RecordRepository,
    private val savedGameRepository: SavedGameRepository,
    private val dailyChallengeRepository: DailyChallengeRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    val backupUri = appSettingsManager.backupUri.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = ""
    )

    var backupData by mutableStateOf<BackupData?>(null)
    private var backupJson: String? = null
    var restoreError by mutableStateOf(false)
    var restoreExceptionString by mutableStateOf("")

    val autoBackupsNumber = appSettingsManager.autoBackupsNumber
    val autoBackupInterval = appSettingsManager.autoBackupInterval
    val lastBackupDate = appSettingsManager.lastBackupDate
    val lastBackupFailure = appSettingsManager.lastBackupFailure
    val dateFormat = appSettingsManager.dateFormat

    fun createBackup(
        backupSettings: Boolean,
        onCreated: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val boards = boardRepository.getAll().first()
                val folders = folderRepository.getAll().first()
                val records = recordRepository.getAll().first()
                val savedGames = savedGameRepository.getAll().first()
                val dailyChallenges = dailyChallengeRepository.getAll().first()

                backupData = BackupData(
                    appVersionName = BuildConfig.VERSION_NAME + if (FlavorUtil.isFoss()) "-FOSS" else "",
                    appVersionCode = BuildConfig.VERSION_CODE,
                    createdAt = ZonedDateTime.now(),
                    boards = boards,
                    folders = folders,
                    records = records,
                    savedGames = savedGames,
                    dailyChallenges = dailyChallenges,
                    settings = if (backupSettings) SettingsBackup.getSettings(
                        appSettingsManager,
                        themeSettingsManager
                    ) else null
                )

                val json = Json {
                    encodeDefaults = true
                    ignoreUnknownKeys = true
                }
                backupJson = json.encodeToString(backupData)
                onCreated(true)
            } catch (e: Exception) {
                onCreated(false)
            }
        }
    }

    fun setBackupDirectory(uri: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsManager.setBackupUri(uri)
        }
    }

    fun prepareBackupToRestore(
        backupString: String,
        onComplete: () -> Unit
    ) {
        try {
            val json = Json { ignoreUnknownKeys = true }
            backupData = json.decodeFromString<BackupData?>(backupString)
            onComplete()
        } catch (e: Exception) {
            restoreError = true
            restoreExceptionString = e.message.toString()
        }
    }

    fun saveBackupTo(
        outputStream: OutputStream?,
        onComplete: (Throwable?) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            backupJson?.let { backup ->
                try {
                    outputStream?.use {
                        it.write(backup.toByteArray())
                        it.close()
                    }
                    onComplete(null)
                    viewModelScope.launch(Dispatchers.IO) {
                        appSettingsManager.setLastBackupDate(ZonedDateTime.now())
                    }
                } catch (e: Exception) {
                    onComplete(e)
                }
            }
        }
    }

    fun restoreBackup(
        onComplete: () -> Unit
    ) {
        backupData?.let { backup ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // deleting all data from database
                    databaseRepository.resetDb()

                    if (backup.boards.isNotEmpty()) {
                        folderRepository.insert(backup.folders)
                        boardRepository.insert(backup.boards)
                        savedGameRepository.insert(backup.savedGames)
                        recordRepository.insert(backup.records)
                    }

                    if (backup.dailyChallenges.isNotEmpty()) {
                        dailyChallengeRepository.insertAll(backup.dailyChallenges)
                    }

                    backup.settings?.setSettings(appSettingsManager, themeSettingsManager)
                    onComplete()
                } catch (e: Exception) {
                    restoreError = true
                    restoreExceptionString = e.message.toString()
                }
            }
        }
    }

    fun setAutoBackupsNumber(value: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsManager.setAutoBackupsNumber(value)
        }
    }

    fun setAutoBackupInterval(hours: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsManager.setAutoBackupInterval(hours)
        }
    }

    fun clearBackupFailure() {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsManager.clearLastBackupFailure()
        }
    }
}