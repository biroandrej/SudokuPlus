package sk.awisoft.sudokuplus.domain.usecase.folder

import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import javax.inject.Inject

class GetLastSavedGamesAnyFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(gamesCount: Int) = folderRepository.getLastSavedGamesAnyFolder(gamesCount)
}