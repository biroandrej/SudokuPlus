package sk.awisoft.sudokuplus.review

import android.app.Activity

object ReviewManager {
    @Suppress("UNUSED_PARAMETER")
    suspend fun requestReviewIfEligible(
        activity: Activity,
        completedGames: Int
    ) = Unit
}
