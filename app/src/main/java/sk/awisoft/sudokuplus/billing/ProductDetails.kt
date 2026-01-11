package sk.awisoft.sudokuplus.billing

data class ProductDetails(
    val productId: String,
    val title: String,
    val description: String,
    val price: String,
    val priceMicros: Long
)
