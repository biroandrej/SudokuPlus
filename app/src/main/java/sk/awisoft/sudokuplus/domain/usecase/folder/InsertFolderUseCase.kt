package sk.awisoft.sudokuplus.domain.usecase.folder

import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import javax.inject.Inject

class InsertFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend operator fun invoke(folder: Folder) = folderRepository.insert(folder)
}