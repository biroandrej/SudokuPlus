package sk.awisoft.sudokuplus.ui.import_from_file

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImportFromFileScreenNavArgs(
    val fileUri: String?,
    val folderUid: Long = -1,
    val fromDeepLink: Boolean = false
) : Parcelable
