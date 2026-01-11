package sk.awisoft.sudokuplus.ai

import sk.awisoft.sudokuplus.core.Cell

sealed class AIHintResponse {
    data class Success(
        val title: String,
        val explanation: String,
        val targetCell: Cell,
        val helpCells: List<Cell>,
        val suggestedValue: Int
    ) : AIHintResponse()

    data class Error(
        val message: String,
        val isRateLimited: Boolean = false
    ) : AIHintResponse()

    data object Loading : AIHintResponse()
}
