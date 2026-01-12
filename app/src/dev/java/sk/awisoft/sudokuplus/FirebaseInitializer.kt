package sk.awisoft.sudokuplus

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseInitializer {
    fun init(context: Context) {
        // Manually initialize Firebase for dev builds since Google Services plugin is disabled
        if (FirebaseApp.getApps(context).isEmpty()) {
            val options = FirebaseOptions.Builder()
                .setProjectId("sudoku-plus-a70c9")
                .setApplicationId("1:290305450686:android:faaf40b2e7f0b6d28574ef")
                .setApiKey("AIzaSyBhRim8f6fD9hWXMap3_wIpZ3EzMFRJMSM")
                .setStorageBucket("sudoku-plus-a70c9.firebasestorage.app")
                .setGcmSenderId("290305450686")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
    }
}
