package sk.awisoft.sudokuplus.data.database.repository

import androidx.room.withTransaction
import sk.awisoft.sudokuplus.data.database.AppDatabase
import sk.awisoft.sudokuplus.domain.repository.DatabaseRepository

class DatabaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : DatabaseRepository {
    /**
     * Completely resets database. Clearing all tables and primary key sequence
     */
    override suspend fun resetDb() {
        appDatabase.withTransaction {
            appDatabase.clearAllTables()
            appDatabase.databaseDao().clearPrimaryKeyIndex()
        }
    }
}
