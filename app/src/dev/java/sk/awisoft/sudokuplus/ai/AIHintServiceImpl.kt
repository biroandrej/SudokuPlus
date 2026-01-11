package sk.awisoft.sudokuplus.ai

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import sk.awisoft.sudokuplus.core.Cell

@Singleton
class AIHintServiceImpl @Inject constructor() : AIHintService {

    override suspend fun generateHint(request: AIHintRequest): AIHintResponse {
        // Simulate network delay
        delay(1500)

        // Find first empty cell from solved board for mock response
        for (row in request.currentBoard.indices) {
            for (col in request.currentBoard[row].indices) {
                if (request.currentBoard[row][col].value == 0) {
                    val solvedValue = request.solvedBoard[row][col].value
                    return AIHintResponse.Success(
                        title = "[DEV] Mock AI Hint",
                        explanation = "[DEV] This is a mock AI response. In production, Gemini would analyze the board and provide a localized explanation in ${request.languageTag}. The correct value for this cell is $solvedValue.",
                        targetCell = Cell(row = row, col = col, value = solvedValue),
                        helpCells = emptyList(),
                        suggestedValue = solvedValue
                    )
                }
            }
        }

        return AIHintResponse.Error("No empty cells found")
    }

    override fun isAvailable(): Boolean = true
}
