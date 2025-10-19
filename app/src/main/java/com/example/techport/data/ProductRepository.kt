package com.example.techport.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    // Create
    suspend fun addProduct(product: Product): Result<String> {
        return try {
            val docRef = productsCollection.document()
            val productWithId = product.copy(id = docRef.id)
            docRef.set(productWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get all products
    suspend fun getAllProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get products by category
    suspend fun getProductsByCategory(category: String): Result<List<Product>> {
        return try {
            val snapshot = if (category == "All") {
                productsCollection
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            } else {
                productsCollection
                    .whereEqualTo("category", category)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get recommended products
    suspend fun getRecommendedProducts(): Result<List<Product>> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("isRecommended", true)
                .limit(5)
                .get()
                .await()
            val products = snapshot.toObjects(Product::class.java)
            Result.success(products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get single product
    suspend fun getProduct(id: String): Result<Product?> {
        return try {
            val snapshot = productsCollection.document(id).get().await()
            val product = snapshot.toObject(Product::class.java)
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update
    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete
    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            productsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Search products
    suspend fun searchProducts(query: String): Result<List<Product>> {
        return try {
            val snapshot = productsCollection.get().await()
            val products = snapshot.toObjects(Product::class.java)
            val filtered = products.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
            Result.success(filtered)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}