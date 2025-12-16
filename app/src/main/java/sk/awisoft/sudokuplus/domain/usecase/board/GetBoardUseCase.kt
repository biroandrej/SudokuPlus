package sk.awisoft.sudokuplus.domain.usecase.board

import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    suspend operator fun invoke(uid: Long) = boardRepository.get(uid)
}