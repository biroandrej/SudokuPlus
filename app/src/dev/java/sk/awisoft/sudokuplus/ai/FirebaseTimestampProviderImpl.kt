package sk.awisoft.sudokuplus.ai

import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTimestampProviderImpl @Inject constructor() : FirebaseTimestampProvider {

    override suspend fun getServerTimestamp(): Long = System.currentTimeMillis()

    override suspend fun getServerDate(): LocalDate = LocalDate.now()
}
