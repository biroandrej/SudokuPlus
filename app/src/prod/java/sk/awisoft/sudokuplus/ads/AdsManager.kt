package sk.awisoft.sudokuplus.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import sk.awisoft.sudokuplus.BuildConfig

object AdsManager {
    private var interstitialAd: InterstitialAd? = null
    private var isLoadingInterstitial = false
    private var rewardedAd: RewardedAd? = null
    private var isLoadingRewarded = false

    fun preloadInterstitial(context: Context) {
        if (isLoadingInterstitial || interstitialAd != null) return
        isLoadingInterstitial = true
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isLoadingInterstitial = false
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    interstitialAd = null
                    isLoadingInterstitial = false
                }
            }
        )
    }

    fun showInterstitialIfAvailable(activity: Activity) {
        val adToShow =
            interstitialAd ?: run {
                preloadInterstitial(activity)
                return
            }
        adToShow.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    preloadInterstitial(activity)
                }

                override fun onAdFailedToShowFullScreenContent(
                    adError: com.google.android.gms.ads.AdError
                ) {
                    interstitialAd = null
                    preloadInterstitial(activity)
                }
            }
        adToShow.show(activity)
        interstitialAd = null
    }

    fun preloadRewarded(context: Context) {
        if (isLoadingRewarded || rewardedAd != null) return
        isLoadingRewarded = true
        RewardedAd.load(
            context,
            BuildConfig.ADMOB_REWARDED_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoadingRewarded = false
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    rewardedAd = null
                    isLoadingRewarded = false
                }
            }
        )
    }

    fun showRewardedIfAvailable(activity: Activity, onReward: () -> Unit): Boolean {
        val adToShow =
            rewardedAd ?: run {
                preloadRewarded(activity)
                return false
            }
        adToShow.fullScreenContentCallback =
            object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    rewardedAd = null
                    preloadRewarded(activity)
                }

                override fun onAdFailedToShowFullScreenContent(
                    adError: com.google.android.gms.ads.AdError
                ) {
                    rewardedAd = null
                    preloadRewarded(activity)
                }
            }
        adToShow.show(activity) {
            onReward()
        }
        rewardedAd = null
        return true
    }

    @Suppress("ktlint:standard:function-naming")
    @Composable
    fun BannerAd(modifier: Modifier) {
        val context = LocalContext.current
        val adView =
            remember {
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    setAdUnitId(BuildConfig.ADMOB_BANNER_AD_UNIT_ID)
                    loadAd(AdRequest.Builder().build())
                }
            }
        AndroidView(
            modifier = modifier,
            factory = { adView },
            update = { }
        )
        DisposableEffect(Unit) {
            onDispose { adView.destroy() }
        }
    }
}
