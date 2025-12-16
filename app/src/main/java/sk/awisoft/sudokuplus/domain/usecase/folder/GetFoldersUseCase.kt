package sk.awisoft.sudokuplus.domain.usecase.folder

import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFoldersUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(): Flow<List<Folder>> = folderRepository.getAll()
}