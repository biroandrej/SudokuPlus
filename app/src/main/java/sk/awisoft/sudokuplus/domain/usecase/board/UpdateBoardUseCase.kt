package sk.awisoft.sudokuplus.domain.usecase.board

import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class UpdateBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(board: SudokuBoard) = boardRepository.update(board)
}