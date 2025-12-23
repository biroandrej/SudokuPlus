package sk.awisoft.sudokuplus

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsInitializer {
    fun init() {
        FirebaseCrashlytics.getInstance().apply {
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        }
    }
}
