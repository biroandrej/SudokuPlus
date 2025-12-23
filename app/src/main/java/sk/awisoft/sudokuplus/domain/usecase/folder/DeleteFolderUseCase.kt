package sk.awisoft.sudokuplus.domain.usecase.folder

import javax.inject.Inject
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.domain.repository.FolderRepository

class DeleteFolderUseCase
@Inject
constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folder: Folder) = folderRepository.delete(folder)
}
