package sk.awisoft.sudokuplus.domain.usecase.folder

import javax.inject.Inject
import sk.awisoft.sudokuplus.domain.repository.FolderRepository

class GetLastSavedGamesAnyFolderUseCase
@Inject
constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(gamesCount: Int) = folderRepository.getLastSavedGamesAnyFolder(gamesCount)
}
