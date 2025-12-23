package sk.awisoft.sudokuplus.domain.usecase.board

import javax.inject.Inject
import sk.awisoft.sudokuplus.domain.repository.BoardRepository

class GetBoardUseCase
@Inject
constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(uid: Long) = boardRepository.get(uid)
}
