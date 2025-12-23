package sk.awisoft.sudokuplus.data.database.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import sk.awisoft.sudokuplus.data.database.dao.FolderDao
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.domain.repository.FolderRepository

class FolderRepositoryImpl(
    private val folderDao: FolderDao
) : FolderRepository {
    override fun getAll(): Flow<List<Folder>> = folderDao.get()

    override fun get(uid: Long): Flow<Folder> = folderDao.get(uid)

    override fun countPuzzlesFolder(uid: Long): Long = folderDao.countPuzzlesFolder(uid)

    override fun getLastSavedGamesAnyFolder(gamesCount: Int): Flow<List<SavedGame>> =
        flowOf() // folderDao.getLastSavedGamesAnyFolder(gamesCount)

    override suspend fun insert(folder: Folder): Long = folderDao.insert(folder)

    override suspend fun insert(folders: List<Folder>): List<Long> = folderDao.insert(folders)

    override suspend fun update(folder: Folder) = folderDao.update(folder)

    override suspend fun delete(folder: Folder) = folderDao.delete(folder)
}
