package sk.awisoft.sudokuplus.ui.gameshistory.savedgame

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SavedGameScreenNavArgs(
    val gameUid: Long
) : Parcelable
