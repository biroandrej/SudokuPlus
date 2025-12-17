package sk.awisoft.sudokuplus.core.qqwing

import sk.awisoft.sudokuplus.core.Cell
import kotlinx.serialization.Serializable

@Serializable
data class Cage(
    val id: Int = 0,
    val sum: Int = 0,
    val cells: List<Cell> = emptyList()
) {
    fun size() = cells.size
}