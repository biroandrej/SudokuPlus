package sk.awisoft.sudokuplus.core.xp

import androidx.annotation.StringRes
import sk.awisoft.sudokuplus.R

enum class LevelTitle(
    val minLevel: Int,
    val maxLevel: Int,
    @param:StringRes val titleRes: Int
) {
    BEGINNER(1, 5, R.string.level_title_beginner),
    NOVICE(6, 10, R.string.level_title_novice),
    APPRENTICE(11, 20, R.string.level_title_apprentice),
    SOLVER(21, 30, R.string.level_title_solver),
    EXPERT(31, 40, R.string.level_title_expert),
    MASTER(41, 50, R.string.level_title_master),
    GRANDMASTER(51, Int.MAX_VALUE, R.string.level_title_grandmaster)
    ;

    companion object {
        fun fromLevel(level: Int): LevelTitle {
            return entries.find { level in it.minLevel..it.maxLevel } ?: BEGINNER
        }
    }
}
