package sk.awisoft.sudokuplus.core.whatsnew

import sk.awisoft.sudokuplus.R

object WhatsNewRepository {

    val allEntries: List<WhatsNewEntry> = listOf(
        WhatsNewEntry(
            versionCode = 15,
            versionName = "1.3.0",
            pages = listOf(
                WhatsNewPage(
                    titleRes = R.string.whats_new_v15_page1_title,
                    descriptionRes = R.string.whats_new_v15_page1_desc,
                    lottieRes = R.raw.whats_new_seasonal_events
                ),
                WhatsNewPage(
                    titleRes = R.string.whats_new_v15_page2_title,
                    descriptionRes = R.string.whats_new_v15_page2_desc,
                    lottieRes = R.raw.whats_new_event_challenges
                ),
                WhatsNewPage(
                    titleRes = R.string.whats_new_v15_page3_title,
                    descriptionRes = R.string.whats_new_v15_page3_desc,
                    lottieRes = R.raw.whats_new_celebrations
                )
            )
        )
    ).sortedBy { it.versionCode }

    fun getLatestEntry(): WhatsNewEntry? = allEntries.lastOrNull()

    fun getEntryForVersion(versionCode: Int): WhatsNewEntry? =
        allEntries.find { it.versionCode == versionCode }

    fun getEntriesNewerThan(versionCode: Int): List<WhatsNewEntry> =
        allEntries.filter { it.versionCode > versionCode }
}
