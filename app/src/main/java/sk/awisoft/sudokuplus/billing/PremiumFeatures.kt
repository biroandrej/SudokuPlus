package sk.awisoft.sudokuplus.billing

object PremiumFeatures {
    const val PRODUCT_ID_MONTHLY = "premium_monthly"
    const val PRODUCT_ID_YEARLY = "premium_yearly"

    fun hasUnlimitedAIHints(status: SubscriptionStatus) = status.isPremium
    fun hasAdFreeExperience(status: SubscriptionStatus) = status.isPremium
    fun hasCloudSync(status: SubscriptionStatus) = status.isPremium
    fun hasExclusiveThemes(status: SubscriptionStatus) = status.isPremium
}
