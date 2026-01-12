package sk.awisoft.sudokuplus.billing

object PremiumFeatures {
    const val PRODUCT_ID_MONTHLY = "premium_monthly"
    const val PRODUCT_ID_YEARLY = "premium_yearly"

    // Google Play RSA public key for purchase verification
    const val PLAY_LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2wM18qH5a4lyfDtEpEMvUdGpW8JrfedovJJEfAdM2LVgJoq4SqZytPISYh+XGnTFTHGCXlPjWowp2F7B1RdNrvFEspqjRX/8S4XbdT5uNrqdbduUW+VhBtPv666gKuUTDfJGg1WUnIPDYDtrbCxeYCSbkTOl6FxizMaRqC5prRxt7cEkzwnXJa07wciBh9a6cWdmQ5mPFiCKWXzy1vFo4TxA0b/FPGTCxafqT5bhg0dKcW7cKsxxGcwk1BTJJ0ZIYhmLsN4ILrFzSjky+i5p93RKZVs0eFcRRoJaLYh8mVCTqNo9ktdJwUYHt6+WiTw8xVqI/ricjURZO792Oz6AiwIDAQAB"

    fun hasUnlimitedAIHints(status: SubscriptionStatus) = status.isPremium
    fun hasAdFreeExperience(status: SubscriptionStatus) = status.isPremium
    fun hasCloudSync(status: SubscriptionStatus) = status.isPremium
    fun hasExclusiveThemes(status: SubscriptionStatus) = status.isPremium
}
