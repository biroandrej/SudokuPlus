package sk.awisoft.sudokuplus.billing

enum class SubscriptionTier {
    FREE,
    PREMIUM
}

data class SubscriptionStatus(
    val tier: SubscriptionTier = SubscriptionTier.FREE,
    val isPremium: Boolean = false,
    val expiryTimestamp: Long? = null,
    val productId: String? = null
)
