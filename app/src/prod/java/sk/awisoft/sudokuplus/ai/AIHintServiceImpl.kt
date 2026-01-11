package sk.awisoft.sudokuplus.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.generationConfig
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import sk.awisoft.sudokuplus.core.Cell

@Singleton
class AIHintServiceImpl @Inject constructor() : AIHintService {

    private val json = Json { ignoreUnknownKeys = true }

    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.vertexAI())
            .generativeModel(
                modelName = "gemini-2.0-flash",
                generationConfig = generationConfig {
                    maxOutputTokens = 500
                    temperature = 0.3f
                }
            )
    }

    override suspend fun generateHint(request: AIHintRequest): AIHintResponse {
        return try {
            val prompt = buildPrompt(request)
            val response = generativeModel.generateContent(prompt)
            parseResponse(response.text ?: "", request)
        } catch (e: Exception) {
            AIHintResponse.Error(
                message = e.message ?: "Unknown error",
                isRateLimited = e.message?.contains("quota", ignoreCase = true) == true ||
                    e.message?.contains("rate", ignoreCase = true) == true
            )
        }
    }

    private fun buildPrompt(request: AIHintRequest): String {
        val boardString = formatBoardForAI(request.currentBoard)
        val notesString = formatNotesForAI(request)
        val languageName = getLanguageName(request.languageTag)

        return """
You are a Sudoku tutor helping a player solve a puzzle. Analyze the current board state and provide the next logical step.

IMPORTANT: You MUST respond entirely in $languageName language. All text including the technique name and explanation must be in $languageName.

Current board state (0 means empty cell):
$boardString

Current notes/candidates for empty cells:
$notesString

Game type: ${request.gameType.name}
Difficulty: ${request.difficulty.name}
Board size: ${request.gameType.size}x${request.gameType.size}

Find the easiest next logical step. Look for these techniques in order:
1. Full House - A row, column, or box with only one empty cell
2. Naked Single - A cell where only one number is possible based on candidates
3. Hidden Single - A number that can only go in one place in a row/column/box
4. Pointing Pairs/Triples - Candidates in a box that point to eliminations
5. More advanced techniques if simpler ones don't apply

Respond ONLY with valid JSON in this exact format (no markdown, no extra text):
{"technique":"technique name in $languageName","targetRow":row_number,"targetCol":col_number,"value":number_to_place,"explanation":"brief explanation in $languageName","helpCells":[[row,col],[row,col]]}

The helpCells array should contain [row, col] pairs of cells that support your reasoning (e.g., cells that eliminate candidates).
Row and column numbers are 0-indexed.
        """.trimIndent()
    }

    private fun formatBoardForAI(board: List<List<Cell>>): String {
        return board.joinToString("\n") { row ->
            row.joinToString(" ") { cell ->
                if (cell.value == 0) "." else cell.value.toString()
            }
        }
    }

    private fun formatNotesForAI(request: AIHintRequest): String {
        val notesByCell = request.notes.groupBy { it.row to it.col }
        if (notesByCell.isEmpty()) {
            return "No notes entered by player"
        }
        return notesByCell.entries.joinToString("\n") { (pos, notes) ->
            "Cell [${pos.first},${pos.second}]: ${notes.map { it.value }.sorted().joinToString(
                ","
            )}"
        }
    }

    private fun parseResponse(responseText: String, request: AIHintRequest): AIHintResponse {
        return try {
            val cleanedJson = responseText
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = json.decodeFromString<AIResponseJson>(cleanedJson)

            val targetCell = Cell(
                row = parsed.targetRow,
                col = parsed.targetCol,
                value = parsed.value
            )

            val helpCells = parsed.helpCells.map { coords ->
                Cell(row = coords[0], col = coords[1], value = 0)
            }

            AIHintResponse.Success(
                title = parsed.technique,
                explanation = parsed.explanation,
                targetCell = targetCell,
                helpCells = helpCells,
                suggestedValue = parsed.value
            )
        } catch (e: Exception) {
            AIHintResponse.Error(
                message = "Failed to parse AI response: ${e.message}"
            )
        }
    }

    private fun getLanguageName(tag: String): String {
        val primaryTag = tag.split("-").firstOrNull()?.lowercase() ?: "en"
        return when (primaryTag) {
            "en" -> "English"
            "de" -> "German"
            "fr" -> "French"
            "es" -> "Spanish"
            "sk" -> "Slovak"
            "cs" -> "Czech"
            "ja" -> "Japanese"
            "zh" -> "Chinese"
            "ru" -> "Russian"
            "pt" -> "Portuguese"
            "it" -> "Italian"
            "pl" -> "Polish"
            "ar" -> "Arabic"
            "hi" -> "Hindi"
            "tr" -> "Turkish"
            "uk" -> "Ukrainian"
            "vi" -> "Vietnamese"
            "ro" -> "Romanian"
            "hu" -> "Hungarian"
            "fi" -> "Finnish"
            "da" -> "Danish"
            "nb", "nn" -> "Norwegian"
            "ca" -> "Catalan"
            "el" -> "Greek"
            "fa" -> "Persian"
            "ta" -> "Tamil"
            "in" -> "Indonesian"
            else -> "English"
        }
    }

    override fun isAvailable(): Boolean = true
}

@Serializable
private data class AIResponseJson(
    val technique: String,
    val targetRow: Int,
    val targetCol: Int,
    val value: Int,
    val explanation: String,
    val helpCells: List<List<Int>> = emptyList()
)
