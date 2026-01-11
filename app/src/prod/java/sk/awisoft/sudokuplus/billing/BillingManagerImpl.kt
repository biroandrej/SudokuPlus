package sk.awisoft.sudokuplus.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetailsResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

@Singleton
class BillingManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BillingManager, PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _subscriptionStatus = MutableStateFlow(SubscriptionStatus())
    override val subscriptionStatus: StateFlow<SubscriptionStatus> = _subscriptionStatus.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    override val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .enablePrepaidPlans()
                .build()
        )
        .build()

    init {
        // Start connection immediately on creation
        scope.launch {
            connectAndQueryPurchases()
        }
    }

    override suspend fun initialize() {
        // Now a no-op since initialization happens in init block
        // Kept for interface compatibility - callers can still call it safely
        if (!billingClient.isReady) {
            connectAndQueryPurchases()
        }
    }

    private suspend fun connectAndQueryPurchases() {
        suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(
                    billingResult: com.android.billingclient.api.BillingResult
                ) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        scope.launch {
                            queryExistingPurchases()
                        }
                    }
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Try to reconnect on next operation
                }
            })
        }
    }

    private suspend fun queryExistingPurchases() {
        if (!billingClient.isReady) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        val result = billingClient.queryPurchasesAsync(params)

        if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            val activePurchase = result.purchasesList.find { purchase ->
                purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                    (
                        purchase.products.contains(PremiumFeatures.PRODUCT_ID_MONTHLY) ||
                            purchase.products.contains(PremiumFeatures.PRODUCT_ID_YEARLY)
                        )
            }

            if (activePurchase != null) {
                // Acknowledge if needed
                if (!activePurchase.isAcknowledged) {
                    acknowledgePurchase(activePurchase)
                }

                _subscriptionStatus.value = SubscriptionStatus(
                    tier = SubscriptionTier.PREMIUM,
                    isPremium = true,
                    productId = activePurchase.products.firstOrNull()
                )
                _isPremium.value = true
            } else {
                _subscriptionStatus.value = SubscriptionStatus()
                _isPremium.value = false
            }
        }
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        val params = com.android.billingclient.api.AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params)
    }

    override suspend fun queryProducts(): List<ProductDetails> {
        if (!billingClient.isReady) {
            connectAndQueryPurchases()
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PremiumFeatures.PRODUCT_ID_MONTHLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PremiumFeatures.PRODUCT_ID_YEARLY)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result: ProductDetailsResult = billingClient.queryProductDetails(params)

        return result.productDetailsList?.map { details ->
            val pricingPhase = details.subscriptionOfferDetails
                ?.firstOrNull()
                ?.pricingPhases
                ?.pricingPhaseList
                ?.firstOrNull()

            ProductDetails(
                productId = details.productId,
                title = details.title,
                description = details.description,
                price = pricingPhase?.formattedPrice ?: "",
                priceMicros = pricingPhase?.priceAmountMicros ?: 0
            )
        } ?: emptyList()
    }

    override suspend fun launchPurchaseFlow(activity: Activity, productId: String): BillingResult {
        if (!billingClient.isReady) {
            return BillingResult.Error("Billing not ready")
        }

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        val result = billingClient.queryProductDetails(params)
        val productDetails = result.productDetailsList?.firstOrNull()
            ?: return BillingResult.Error("Product not found")

        val offerToken = productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken
            ?: return BillingResult.Error("No offer available")

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

        return when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> BillingResult.FlowLaunched
            BillingClient.BillingResponseCode.USER_CANCELED -> BillingResult.Cancelled
            else -> BillingResult.Error(billingResult.debugMessage)
        }
    }

    override suspend fun restorePurchases() {
        queryExistingPurchases()
    }

    override fun disconnect() {
        billingClient.endConnection()
    }

    override fun onPurchasesUpdated(
        billingResult: com.android.billingclient.api.BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    scope.launch {
                        handlePurchase(purchase)
                    }
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // User cancelled, no action needed
            }
            else -> {
                // Handle error
            }
        }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            }

            _subscriptionStatus.value = SubscriptionStatus(
                tier = SubscriptionTier.PREMIUM,
                isPremium = true,
                productId = purchase.products.firstOrNull()
            )
            _isPremium.value = true
        }
    }
}
