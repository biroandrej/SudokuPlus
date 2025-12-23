package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.SavedGame

interface FolderRepository {
    fun getAll(): Flow<List<Folder>>

    fun get(uid: Long): Flow<Folder>

    fun countPuzzlesFolder(uid: Long): Long

    fun getLastSavedGamesAnyFolder(gamesCount: Int): Flow<List<SavedGame>>

    suspend fun insert(folder: Folder): Long

    suspend fun insert(folders: List<Folder>): List<Long>

    suspend fun update(folder: Folder)

    suspend fun delete(folder: Folder)
}
