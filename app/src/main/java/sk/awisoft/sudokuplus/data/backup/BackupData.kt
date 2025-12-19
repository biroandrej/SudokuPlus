package sk.awisoft.sudokuplus.data.backup

import sk.awisoft.sudokuplus.data.backup.serializer.ZonedDateTimeLongSerializer
import sk.awisoft.sudokuplus.data.database.model.DailyChallenge
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.Record
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

const val BACKUP_SCHEME_VERSION = 2

@Serializable
data class BackupData(
    val appVersionName: String,
    val appVersionCode: Int,
    val backupSchemeVersion: Int = BACKUP_SCHEME_VERSION,
    @Serializable(with = ZonedDateTimeLongSerializer::class)
    val createdAt: ZonedDateTime,
    val boards: List<SudokuBoard>,
    val records: List<Record> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val savedGames: List<SavedGame>,
    val dailyChallenges: List<DailyChallenge> = emptyList(),
    val settings: SettingsBackup? = null
) {
    companion object {
        /**
         * Regex for auto backups
         */
        val regexAuto = """SudokuPlus-AutoBackup-\d+-\d+-\d+--\d+-\d+-\d+.json""".toRegex()

        /**
         * Filename for manual backups
         */
        val nameManual: String
            get() = "SudokuPlus-Backup-${
                ZonedDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"))

            }"

        /**
         * Filename for auto backups
         */
        val nameAuto: String
            get() = "SudokuPlus-AutoBackup-${
            ZonedDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH-mm-ss"))
        }"
    }
}