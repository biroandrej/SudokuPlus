package sk.awisoft.sudokuplus.billing

import android.app.Activity
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class BillingManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : BillingManager {

    // Dev builds always have premium for testing
    private val _subscriptionStatus = MutableStateFlow(
        SubscriptionStatus(
            tier = SubscriptionTier.PREMIUM,
            isPremium = true
        )
    )
    override val subscriptionStatus: StateFlow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

    private val _isPremium = MutableStateFlow(true)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    override suspend fun initialize() {
        // No-op in dev
    }

    override suspend fun queryProducts(): List<ProductDetails> = listOf(
        ProductDetails(
            productId = PremiumFeatures.PRODUCT_ID_MONTHLY,
            title = "[DEV] Premium Monthly",
            description = "[DEV] All premium features - monthly subscription",
            price = "$4.99/month",
            priceMicros = 4990000
        ),
        ProductDetails(
            productId = PremiumFeatures.PRODUCT_ID_YEARLY,
            title = "[DEV] Premium Yearly",
            description = "[DEV] All premium features - yearly subscription",
            price = "$39.99/year",
            priceMicros = 39990000
        )
    )

    override suspend fun launchPurchaseFlow(activity: Activity, productId: String): BillingResult {
        return BillingResult.FlowLaunched // Always succeeds in dev
    }

    override suspend fun restorePurchases() {
        // No-op in dev
    }

    override fun disconnect() {
        // No-op in dev
    }
}
