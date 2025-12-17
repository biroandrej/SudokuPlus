package sk.awisoft.sudokuplus.domain.usecase.board

import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class GetBoardsInFolderUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(uid: Long) = boardRepository.getBoardsInFolder(uid)
}