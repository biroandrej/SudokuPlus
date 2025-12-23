package sk.awisoft.sudokuplus.domain.usecase.folder

import javax.inject.Inject
import sk.awisoft.sudokuplus.domain.repository.FolderRepository

class GetFolderUseCase
@Inject
constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(uid: Long) = folderRepository.get(uid)
}
