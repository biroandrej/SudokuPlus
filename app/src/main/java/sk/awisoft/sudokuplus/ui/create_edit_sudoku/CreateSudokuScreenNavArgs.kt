package sk.awisoft.sudokuplus.ui.create_edit_sudoku

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateSudokuScreenNavArgs(
    val gameUid: Long = -1,
    val folderUid: Long? = null
) : Parcelable
