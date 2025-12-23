package sk.awisoft.sudokuplus.domain.usecase.board

import javax.inject.Inject
import sk.awisoft.sudokuplus.domain.repository.BoardRepository

class GetGamesInFolderUseCase
@Inject
constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(folderUid: Long) = boardRepository.getAllInFolderList(folderUid)
}
