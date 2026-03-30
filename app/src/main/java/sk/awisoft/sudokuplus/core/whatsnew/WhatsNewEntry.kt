package sk.awisoft.sudokuplus.core.whatsnew

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes

data class WhatsNewEntry(
    val versionCode: Int,
    val versionName: String,
    val pages: List<WhatsNewPage>
)

data class WhatsNewPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val illustrationRes: Int? = null,
    @RawRes val lottieRes: Int? = null
)
