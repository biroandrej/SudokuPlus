package sk.awisoft.sudokuplus.core.startup

import android.content.Context
import androidx.startup.Initializer
import sk.awisoft.sudokuplus.BuildConfig
import sk.awisoft.sudokuplus.core.utils.GlobalExceptionHandler
import sk.awisoft.sudokuplus.ui.app_crash.CrashActivity

class CrashHandlerInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        if (!BuildConfig.DEBUG) {
            GlobalExceptionHandler.initialize(context, CrashActivity::class.java)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
