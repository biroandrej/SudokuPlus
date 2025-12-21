package sk.awisoft.sudokuplus.ui.explore_folder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExploreFolderScreenNavArgs(
    val folderUid: Long
) : Parcelable