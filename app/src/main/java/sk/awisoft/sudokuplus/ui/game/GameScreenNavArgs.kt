package sk.awisoft.sudokuplus.ui.game

data class GameScreenNavArgs(
    val gameUid: Long,
    val playedBefore: Boolean = false,
    val isDailyChallenge: Boolean = false
)
