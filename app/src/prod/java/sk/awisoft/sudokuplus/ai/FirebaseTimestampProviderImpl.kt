package sk.awisoft.sudokuplus.ai

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseTimestampProviderImpl @Inject constructor() : FirebaseTimestampProvider {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override suspend fun getServerTimestamp(): Long {
        return try {
            val docRef = firestore.collection("_timestamps").document()
            docRef.set(mapOf("timestamp" to FieldValue.serverTimestamp())).await()
            val snapshot = docRef.get().await()
            val timestamp = snapshot.getTimestamp("timestamp")
            docRef.delete() // Clean up
            timestamp?.toDate()?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            // Fallback to local time if Firebase is unavailable
            System.currentTimeMillis()
        }
    }

    override suspend fun getServerDate(): LocalDate {
        val timestamp = getServerTimestamp()
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
}
