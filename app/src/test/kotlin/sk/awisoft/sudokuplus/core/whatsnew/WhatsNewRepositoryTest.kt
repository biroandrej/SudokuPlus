package sk.awisoft.sudokuplus.core.whatsnew

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WhatsNewRepositoryTest {

    @Test
    fun `getLatestEntry should return the entry with highest version code`() {
        val latest = WhatsNewRepository.getLatestEntry()
        assertNotNull(latest)
        val allEntries = WhatsNewRepository.allEntries
        assertTrue(allEntries.all { it.versionCode <= latest!!.versionCode })
    }

    @Test
    fun `every entry should have at least one page`() {
        WhatsNewRepository.allEntries.forEach { entry ->
            assertTrue(
                "Entry ${entry.versionName} has no pages",
                entry.pages.isNotEmpty()
            )
        }
    }

    @Test
    fun `entries should be sorted by version code ascending`() {
        val entries = WhatsNewRepository.allEntries
        for (i in 1 until entries.size) {
            assertTrue(entries[i].versionCode > entries[i - 1].versionCode)
        }
    }
}
