package sk.awisoft.sudokuplus.core.startup

import android.content.Context
import androidx.startup.Initializer
import sk.awisoft.sudokuplus.ads.AdsInitializer

class AdsStartupInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AdsInitializer.initialize(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CrashHandlerInitializer::class.java
    )
}
