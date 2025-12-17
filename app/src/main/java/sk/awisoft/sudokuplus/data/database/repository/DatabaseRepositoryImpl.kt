package sk.awisoft.sudokuplus.data.database.repository

import sk.awisoft.sudokuplus.data.database.AppDatabase
import sk.awisoft.sudokuplus.domain.repository.DatabaseRepository
import kotlinx.coroutines.runBlocking

class DatabaseRepositoryImpl(
    private val appDatabase: AppDatabase
) : DatabaseRepository {
    /**
     * Completely resets database. Clearing all tables and primary key sequence
     */
    override suspend fun resetDb() {
        appDatabase.runInTransaction {
            runBlocking {
                appDatabase.clearAllTables()
                appDatabase.databaseDao().clearPrimaryKeyIndex()
            }
        }
    }
}