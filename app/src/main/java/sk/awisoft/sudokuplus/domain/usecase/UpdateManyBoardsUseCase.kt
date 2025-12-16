package sk.awisoft.sudokuplus.domain.usecase

import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class UpdateManyBoardsUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(boards: List<SudokuBoard>) = boardRepository.update(boards)
}