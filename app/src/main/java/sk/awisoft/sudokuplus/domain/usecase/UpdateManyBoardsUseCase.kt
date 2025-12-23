package sk.awisoft.sudokuplus.domain.usecase

import javax.inject.Inject
import sk.awisoft.sudokuplus.data.database.model.SudokuBoard
import sk.awisoft.sudokuplus.domain.repository.BoardRepository

class UpdateManyBoardsUseCase
@Inject
constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(boards: List<SudokuBoard>) = boardRepository.update(boards)
}
