package sk.awisoft.sudokuplus

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import sk.awisoft.sudokuplus.ads.AdsInitializer
import javax.inject.Inject

@HiltAndroidApp
class SudokuPlusApp : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        AdsInitializer.initialize(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()

}
