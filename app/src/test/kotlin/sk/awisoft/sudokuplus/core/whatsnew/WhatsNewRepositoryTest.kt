package sk.awisoft.sudokuplus.core.whatsnew

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun `getEntryForVersion should return entry for known version`() {
        val latest = WhatsNewRepository.getLatestEntry()!!
        val entry = WhatsNewRepository.getEntryForVersion(latest.versionCode)
        assertNotNull(entry)
        assertEquals(latest.versionCode, entry!!.versionCode)
    }

    @Test
    fun `getEntryForVersion should return null for unknown version`() {
        val entry = WhatsNewRepository.getEntryForVersion(0)
        assertNull(entry)
    }

    @Test
    fun `getEntriesNewerThan should return entries after given version`() {
        val entries = WhatsNewRepository.getEntriesNewerThan(0)
        assertTrue(entries.isNotEmpty())
        assertTrue(entries.all { it.versionCode > 0 })
    }

    @Test
    fun `getEntriesNewerThan should return empty for latest version`() {
        val latest = WhatsNewRepository.getLatestEntry()!!
        val entries = WhatsNewRepository.getEntriesNewerThan(latest.versionCode)
        assertTrue(entries.isEmpty())
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
