package com.example.techport.data


import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val rating: Double = 0.0,
    val isRecommended: Boolean = false,
    val specifications: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)

// Cart Item
data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val totalPrice: Double
        get() = product.price * quantity
}

// API Response Model for external API (Fake Store API)
@Serializable
data class ExternalProduct(
    val id: Int = 0,
    val title: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val image: String = "",
    val rating: Rating = Rating()
)

@Serializable
data class Rating(
    val rate: Double = 0.0,
    val count: Int = 0
)

// Categories
object ProductCategories {
    val categories = listOf(
        "All",
        "Electronics",
        "Smartphones",
        "Laptops",
        "Accessories",
        "Audio",
        "Cameras",
        "Wearables"
    )
}