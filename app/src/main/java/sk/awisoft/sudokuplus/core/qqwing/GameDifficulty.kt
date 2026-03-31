package sk.awisoft.sudokuplus.core.qqwing

import sk.awisoft.sudokuplus.R

enum class GameDifficulty(val value: String, val resName: Int) {
    Unspecified("unspecified", R.string.difficulty_unspecified),
    Simple("simple", R.string.difficulty_simple),
    Easy("easy", R.string.difficulty_easy),
    Moderate("moderate", R.string.difficulty_moderate),
    Hard("hard", R.string.difficulty_hard),
    Challenge("challenge", R.string.difficulty_challenge),
    Custom("custom", R.string.difficulty_custom);

    companion object {
        fun find(value: String): GameDifficulty? = entries.find { it.value == value }

        fun get(value: String): GameDifficulty =
            find(value) ?: throw IllegalArgumentException("Unknown GameDifficulty: $value")
    }
}
