package sk.awisoft.sudokuplus.domain.usecase.folder

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import sk.awisoft.sudokuplus.data.database.model.Folder
import sk.awisoft.sudokuplus.domain.repository.FolderRepository

class GetFoldersUseCase
@Inject
constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(): Flow<List<Folder>> = folderRepository.getAll()
}
