package sk.awisoft.sudokuplus.billing

/**
 * Result of launching the billing flow.
 * Note: [FlowLaunched] indicates the purchase UI was shown successfully,
 * NOT that the purchase completed. Actual purchase results are delivered
 * asynchronously via [BillingManager.subscriptionStatus] and [BillingManager.isPremium].
 */
sealed class BillingResult {
    /** Billing flow was launched successfully. Purchase result is async. */
    data object FlowLaunched : BillingResult()

    /** User cancelled the billing flow. */
    data object Cancelled : BillingResult()

    /** Error occurred before/during launching the flow. */
    data class Error(val message: String) : BillingResult()
}
