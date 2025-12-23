package sk.awisoft.sudokuplus.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object AdsManager {
    fun preloadInterstitial(context: Context) = Unit

    fun showInterstitialIfAvailable(activity: Activity) = Unit

    fun preloadRewarded(context: Context) = Unit

    fun showRewardedIfAvailable(activity: Activity, onReward: () -> Unit): Boolean = false

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun BannerAd(modifier: Modifier = Modifier) = Unit
}
