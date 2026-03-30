package sk.awisoft.sudokuplus

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseInitializer {
    fun init(context: Context) {
        // Manually initialize Firebase for dev builds since Google Services plugin is disabled
        // Configuration is loaded from firebase.properties via BuildConfig
        if (FirebaseApp.getApps(context).isEmpty() && BuildConfig.FIREBASE_API_KEY.isNotEmpty()) {
            val options = FirebaseOptions.Builder()
                .setProjectId(BuildConfig.FIREBASE_PROJECT_ID)
                .setApplicationId(BuildConfig.FIREBASE_APPLICATION_ID)
                .setApiKey(BuildConfig.FIREBASE_API_KEY)
                .setStorageBucket(BuildConfig.FIREBASE_STORAGE_BUCKET)
                .setGcmSenderId(BuildConfig.FIREBASE_GCM_SENDER_ID)
                .build()
            FirebaseApp.initializeApp(context, options)
        }

        // Connect to Firestore emulator for local development
        // Use 10.0.2.2 for Android emulator (maps to host localhost)
        if (BuildConfig.DEBUG) {
            try {
                FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
            } catch (e: IllegalStateException) {
                // Already connected to emulator
            }
        }
    }
}
