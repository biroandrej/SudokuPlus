package sk.awisoft.sudokuplus.data.backup

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import sk.awisoft.sudokuplus.BuildConfig
import sk.awisoft.sudokuplus.data.datastore.AppSettingsManager
import sk.awisoft.sudokuplus.data.datastore.ThemeSettingsManager
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import sk.awisoft.sudokuplus.domain.repository.RecordRepository
import sk.awisoft.sudokuplus.domain.repository.SavedGameRepository
import sk.awisoft.sudokuplus.util.FlavorUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@HiltWorker
class BackupWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val appSettingsManager: AppSettingsManager,
    private val boardRepository: BoardRepository,
    private val folderRepository: FolderRepository,
    private val recordRepository: RecordRepository,
    private val savedGameRepository: SavedGameRepository,
    private val themeSettingsManager: ThemeSettingsManager
) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {
        var backupSuccessful = false

        try {
            val backupUri = appSettingsManager.backupUri.first()
            val boards = boardRepository.getAll().first()

            if (backupUri.isEmpty()) {
                Log.i(WORK_NAME_AUTO_BACKUP, "Automatic backup skipped: URI is empty")
                appSettingsManager.setLastBackupFailure(FAILURE_NO_DIRECTORY)
                return Result.failure()
            } else if (boards.isEmpty()) {
                // Nothing to backup is not really a failure, just skip silently
                Log.i(WORK_NAME_AUTO_BACKUP, "Automatic backup skipped: Nothing to backup")
                return Result.success()
            }

            if (!context.contentResolver.persistedUriPermissions.any { it.uri == backupUri.toUri() }) {
                Log.i(WORK_NAME_AUTO_BACKUP, "Automatic backup skipped: not persisted URI")
                appSettingsManager.setLastBackupFailure(FAILURE_PERMISSION_LOST)
                return Result.failure()
            }

            val folders = folderRepository.getAll().first()
            val records = recordRepository.getAll().first()
            val savedGames = savedGameRepository.getAll().first()

            val documentFile = DocumentFile.fromTreeUri(context, backupUri.toUri())
            if (documentFile == null) {
                Log.e(WORK_NAME_AUTO_BACKUP, "Failed to access backup directory")
                appSettingsManager.setLastBackupFailure(FAILURE_DIRECTORY_ACCESS)
                return Result.failure()
            }

            val backupData = BackupData(
                appVersionName = BuildConfig.VERSION_NAME + if (FlavorUtil.isFoss()) "-FOSS" else "",
                appVersionCode = BuildConfig.VERSION_CODE,
                createdAt = ZonedDateTime.now(),
                boards = boards,
                folders = folders,
                records = records,
                savedGames = savedGames,
                settings = SettingsBackup.getSettings(appSettingsManager, themeSettingsManager)
            )

            val json = Json {
                encodeDefaults = true
            }
            val backupJson = json.encodeToString(backupData)

            val file = documentFile.createFile(
                "application/json",
                BackupData.nameAuto
            )

            if (file != null) {
                context.contentResolver.openOutputStream(file.uri).use { outputStream ->
                    outputStream?.write(backupJson.toByteArray())
                    outputStream?.close()
                }
                backupSuccessful = true

                appSettingsManager.setLastBackupDate(ZonedDateTime.now())
                appSettingsManager.clearLastBackupFailure()
            } else {
                Log.e(WORK_NAME_AUTO_BACKUP, "Failed to create backup file")
                appSettingsManager.setLastBackupFailure(FAILURE_FILE_CREATE)
                return Result.failure()
            }

            val autoBackupsNumber = appSettingsManager.autoBackupsNumber.first()

            documentFile.listFiles()
                .filter { BackupData.regexAuto.matches(it.name ?: "") }
                .sortedByDescending { it.name }
                .drop(autoBackupsNumber)
                .forEach { it.delete() }

        } catch (e: java.io.IOException) {
            // I/O errors are transient, retry
            Log.e(WORK_NAME_AUTO_BACKUP, "I/O error during backup: ${e.message}")
            appSettingsManager.setLastBackupFailure(FAILURE_IO_ERROR)
            return Result.retry()
        } catch (e: Exception) {
            Log.e(WORK_NAME_AUTO_BACKUP, e.message.toString())
            appSettingsManager.setLastBackupFailure(FAILURE_UNKNOWN)
            return Result.failure()
        }

        Log.i(
            WORK_NAME_AUTO_BACKUP,
            "Automatic backup created. T=${
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            }"
        )
        return if (backupSuccessful) Result.success() else Result.failure()
    }

    companion object {
        fun setupWorker(context: Context, intervalHours: Long) {
            if (intervalHours < 1) {
                cancelWorker(context)
                return
            }

            val periodicWorkRequest = PeriodicWorkRequest.Builder(
                BackupWorker::class.java,
                Duration.ofHours(intervalHours)
            ).build()

            val workManager = WorkManager.getInstance(context)
            workManager.enqueueUniquePeriodicWork(
                WORK_NAME_AUTO_BACKUP,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                periodicWorkRequest
            )
        }

        fun cancelWorker(context: Context) {
            val workManager = WorkManager.getInstance(context)
            workManager.cancelUniqueWork(WORK_NAME_AUTO_BACKUP)
        }
    }
}

private const val WORK_NAME_AUTO_BACKUP = "AutomaticBackupWorker"

// Backup failure reasons (used as keys for string resources)
const val FAILURE_NO_DIRECTORY = "no_directory"
const val FAILURE_PERMISSION_LOST = "permission_lost"
const val FAILURE_DIRECTORY_ACCESS = "directory_access"
const val FAILURE_FILE_CREATE = "file_create"
const val FAILURE_IO_ERROR = "io_error"
const val FAILURE_UNKNOWN = "unknown"