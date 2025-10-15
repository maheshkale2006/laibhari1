data class ProductModel(
    val productId: String = "",
    val productName: String = "",
    val productDescription: String = "",
    val productPrice: Double = 0.0,
    val category: String = "",
    val imageBase64: String = "",
    val shopOwnerId: String = "",
    val timestamp: Long = 0
)
