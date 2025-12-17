package sk.awisoft.sudokuplus.domain.usecase.folder

import sk.awisoft.sudokuplus.domain.repository.FolderRepository
import javax.inject.Inject

class GetFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    operator fun invoke(uid: Long) = folderRepository.get(uid)
}