package sk.awisoft.sudokuplus.domain.usecase.board

import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardsInFolderFlowUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(uid: Long) = boardRepository.getBoardsInFolderFlow(uid)
}