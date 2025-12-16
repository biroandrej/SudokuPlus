package sk.awisoft.sudokuplus.util

import sk.awisoft.sudokuplus.BuildConfig

object FlavorUtil {
    fun isFoss(): Boolean  = BuildConfig.FLAVOR == "foss"
}