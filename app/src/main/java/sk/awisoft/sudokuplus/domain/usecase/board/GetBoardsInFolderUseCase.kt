package sk.awisoft.sudokuplus.domain.usecase.board

import javax.inject.Inject
import sk.awisoft.sudokuplus.domain.repository.BoardRepository

class GetBoardsInFolderUseCase
@Inject
constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(uid: Long) = boardRepository.getBoardsInFolder(uid)
}
