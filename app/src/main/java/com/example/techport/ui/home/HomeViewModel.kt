package com.example.techport.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techport.data.Product
import com.example.techport.data.ProductRepository
import com.example.techport.data.ApiService
import com.example.techport.data.CartItem
import com.example.techport.data.ExternalProduct
import com.example.techport.ui.history.Purchase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val apiService = ApiService()
    private val analytics = Firebase.analytics
    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    var currentUser by mutableStateOf(auth.currentUser)
        private set

    var products by mutableStateOf<List<Product>>(emptyList())
        private set

    var recommendedProducts by mutableStateOf<List<Product>>(emptyList())
        private set

    var externalProducts by mutableStateOf<List<ExternalProduct>>(emptyList())
        private set

    var cartItems by mutableStateOf<List<CartItem>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadProducts()
        loadRecommendedProducts()
        loadExternalProducts()

        auth.addAuthStateListener {
            currentUser = it.currentUser
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.getProductsByCategory(selectedCategory).fold(
                    onSuccess = { products = it },
                    onFailure = {
                        errorMessage = it.message
                        crashlytics.recordException(it)
                    }
                )
            } finally {
                isLoading = false
            }
        }
    }

    fun loadRecommendedProducts() {
        viewModelScope.launch {
            repository.getRecommendedProducts().fold(
                onSuccess = { recommendedProducts = it },
                onFailure = { crashlytics.recordException(it) }
            )
        }
    }

    fun loadExternalProducts() {
        viewModelScope.launch {
            try {
                externalProducts = apiService.fetchExternalProducts()
                analytics.logEvent("external_api_loaded", bundleOf(
                    "product_count" to externalProducts.size.toLong()
                ))
            } catch (e: Exception) {
                crashlytics.recordException(e)
            }
        }
    }

    fun searchProducts(query: String) {
        searchQuery = query
        if (query.isEmpty()) {
            loadProducts()
            return
        }
        viewModelScope.launch {
            isLoading = true
            repository.searchProducts(query).fold(
                onSuccess = { products = it },
                onFailure = { errorMessage = it.message }
            )
            isLoading = false
        }
    }

    fun selectCategory(category: String) {
        selectedCategory = category
        analytics.logEvent("category_selected", bundleOf("category" to category))
        loadProducts()
    }

    fun addToCart(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
            cartItems = cartItems.toList() // Trigger recomposition
        } else {
            cartItems = cartItems + CartItem(product, 1)
        }
        analytics.logEvent("add_to_cart", bundleOf(
            FirebaseAnalytics.Param.ITEM_ID to product.id,
            FirebaseAnalytics.Param.ITEM_NAME to product.name
        ))
    }

    fun updateCartItemQuantity(cartItem: CartItem, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(cartItem)
        } else {
            cartItem.quantity = quantity
            cartItems = cartItems.toList()
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        cartItems = cartItems.filter { it.product.id != cartItem.product.id }
        analytics.logEvent("remove_from_cart", bundleOf(
            FirebaseAnalytics.Param.ITEM_ID to cartItem.product.id
        ))
    }

    fun clearCart() {
        cartItems = emptyList()
    }

    fun checkout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val userId = currentUser?.uid
                if (userId != null) {
                    cartItems.forEach { cartItem ->
                        val purchase = Purchase(
                            productId = cartItem.product.id,
                            productName = cartItem.product.name,
                            timestamp = System.currentTimeMillis()
                        )
                        db.collection("user").document(userId).collection("purchases").add(purchase)
                    }
                }

                // Update stock for each cart item
                cartItems.forEach { cartItem ->
                    val updatedProduct = cartItem.product.copy(
                        stock = (cartItem.product.stock - cartItem.quantity).coerceAtLeast(0)
                    )
                    repository.updateProduct(updatedProduct)
                }

                analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundleOf(
                    FirebaseAnalytics.Param.TRANSACTION_ID to "T${System.currentTimeMillis()}",
                    FirebaseAnalytics.Param.VALUE to cartItems.sumOf { it.totalPrice },
                    FirebaseAnalytics.Param.CURRENCY to "USD"
                ))

                clearCart()
                loadProducts() // Refresh product list
                onSuccess()
            } catch (e: Exception) {
                crashlytics.recordException(e)
                onError(e.message ?: "Checkout failed")
            } finally {
                isLoading = false
            }
        }
    }

    fun getProductById(id: String): Product? {
        // Check in main products list first
        products.find { it.id == id }?.let { return it }
        // Check in recommended products
        recommendedProducts.find { it.id == id }?.let { return it }
        // Not found
        return null
    }

    fun addProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            repository.addProduct(product).fold(
                onSuccess = {
                    analytics.logEvent("product_added", bundleOf("product_name" to product.name))
                    loadProducts()
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to add product")
                }
            )
            isLoading = false
        }
    }

    fun updateProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            repository.updateProduct(product).fold(
                onSuccess = {
                    analytics.logEvent("product_updated", bundleOf("product_id" to product.id))
                    loadProducts()
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to update product")
                }
            )
            isLoading = false
        }
    }

    fun deleteProduct(productId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            repository.deleteProduct(productId).fold(
                onSuccess = {
                    analytics.logEvent("product_deleted", bundleOf("product_id" to productId))
                    loadProducts()
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to delete product")
                }
            )
            isLoading = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        apiService.close()
    }
}