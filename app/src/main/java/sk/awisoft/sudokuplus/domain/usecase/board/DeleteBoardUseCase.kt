package sk.awisoft.sudokuplus.domain.usecase.board

import javax.inject.Inject
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.domain.repository.BoardRepository

class DeleteBoardUseCase
@Inject
constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(board: SudokuBoard) = boardRepository.delete(board)
}
