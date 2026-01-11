package sk.awisoft.sudokuplus.ai

interface AIHintService {
    suspend fun generateHint(request: AIHintRequest): AIHintResponse
    fun isAvailable(): Boolean
}
