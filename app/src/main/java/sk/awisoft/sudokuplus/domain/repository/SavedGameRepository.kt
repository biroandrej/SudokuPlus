package sk.awisoft.sudokuplus.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.SavedGame
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard

interface SavedGameRepository {
    fun getAll(): Flow<List<SavedGame>>

    suspend fun get(uid: Long): SavedGame?

    fun getWithBoards(): Flow<Map<SavedGame, SudokuBoard>>

    fun getLast(): Flow<SavedGame?>

    fun getLastPlayable(limit: Int): Flow<Map<SavedGame, SudokuBoard>>

    suspend fun insert(savedGame: SavedGame): Long

    suspend fun insert(savedGames: List<SavedGame>)

    suspend fun update(savedGame: SavedGame)

    suspend fun delete(savedGame: SavedGame)
}
