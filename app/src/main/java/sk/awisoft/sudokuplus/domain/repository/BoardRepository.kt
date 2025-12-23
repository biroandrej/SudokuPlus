package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard

interface BoardRepository {
    fun getAll(): Flow<List<SudokuBoard>>

    fun getAll(difficulty: GameDifficulty): Flow<List<SudokuBoard>>

    fun getAllInFolder(folderUid: Long): Flow<List<SudokuBoard>>

    fun getAllInFolderList(folderUid: Long): List<SudokuBoard>

    fun getWithSavedGames(): Flow<Map<SudokuBoard, SavedGame>>

    fun getWithSavedGames(difficulty: GameDifficulty): Flow<Map<SudokuBoard, SavedGame>>

    fun getInFolderWithSaved(folderUid: Long): Flow<Map<SudokuBoard, SavedGame>>

    fun getBoardsInFolderFlow(uid: Long): Flow<List<SudokuBoard>>

    fun getBoardsInFolder(uid: Long): List<SudokuBoard>

    suspend fun get(uid: Long): SudokuBoard

    suspend fun insert(boards: List<SudokuBoard>): List<Long>

    suspend fun insert(board: SudokuBoard): Long

    suspend fun delete(board: SudokuBoard)

    suspend fun delete(boards: List<SudokuBoard>)

    suspend fun update(board: SudokuBoard)

    suspend fun update(boards: List<SudokuBoard>)
}
