package sk.awisoft.sudokuplus.billing

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface BillingManager {
    val subscriptionStatus: StateFlow<SubscriptionStatus>
    val isPremium: StateFlow<Boolean>

    suspend fun initialize()
    suspend fun queryProducts(): List<ProductDetails>
    suspend fun launchPurchaseFlow(activity: Activity, productId: String): BillingResult
    suspend fun restorePurchases()
    fun disconnect()
}
