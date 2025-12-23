package sk.awisoft.sudokuplus.domain.usecase.record

import javax.inject.Inject
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType
import sk.awisoft.sudokuplus.domain.repository.RecordRepository

class GetAllRecordsUseCase
@Inject
constructor(
    private val recordRepository: RecordRepository
) {
    operator fun invoke(difficulty: GameDifficulty, type: GameType) =
        recordRepository.getAll(difficulty, type)
}
