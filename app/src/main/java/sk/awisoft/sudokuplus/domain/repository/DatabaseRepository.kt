package sk.awisoft.sudokuplus.domain.repository

interface DatabaseRepository {
    suspend fun resetDb()
}
