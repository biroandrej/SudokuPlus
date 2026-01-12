package sk.awisoft.sudokuplus.ai

import java.time.LocalDate

interface FirebaseTimestampProvider {
    suspend fun getServerTimestamp(): Long
    suspend fun getServerDate(): LocalDate
}
