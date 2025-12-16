package sk.awisoft.sudokuplus.domain.usecase.board

import sk.awisoft.sudokuplus.domain.repository.BoardRepository
import javax.inject.Inject

class GetGamesInFolderUseCase @Inject constructor(
    private val boardRepository: BoardRepository
) {
    operator fun invoke(folderUid: Long) = boardRepository.getAllInFolderList(folderUid)
}