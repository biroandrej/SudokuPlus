package sk.awisoft.sudokuplus.ai

import sk.awisoft.sudokuplus.core.Cell
import sk.awisoft.sudokuplus.core.Note
import sk.awisoft.sudokuplus.core.qqwing.GameDifficulty
import sk.awisoft.sudokuplus.core.qqwing.GameType

data class AIHintRequest(
    val currentBoard: List<List<Cell>>,
    val solvedBoard: List<List<Cell>>,
    val notes: List<Note>,
    val gameType: GameType,
    val difficulty: GameDifficulty,
    val languageTag: String,
    val targetCell: Cell? = null
)
