package com.example.techport.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techport.data.Product
import com.example.techport.data.Repair
import com.example.techport.data.RepairStatus
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class Purchase(
    val productId: String = "",
    val productName: String = "",
    val timestamp: Long = 0,
    var imageUrl: String = "",
    var repairStatus: RepairStatus? = null
)

class HistoryViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    var purchaseHistory by mutableStateOf<List<Purchase>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun loadPurchaseHistory() {
        viewModelScope.launch {
            isLoading = true
            val userId = auth.currentUser?.uid
            if (userId != null) {
                try {
                    val purchasesSnapshot = db.collection("user").document(userId).collection("purchases")
                        .orderBy("timestamp")
                        .get()
                        .await()

                    val purchases = purchasesSnapshot.documents.map { doc ->
                        val purchase = doc.toObject(Purchase::class.java)!!
                        val productSnapshot = db.collection("products").document(purchase.productId).get().await()
                        val product = productSnapshot.toObject(Product::class.java)
                        purchase.imageUrl = product?.imageUrl ?: ""

                        val repairsSnapshot = db.collection("repairs")
                            .whereEqualTo("userId", userId)
                            .whereEqualTo("productId", purchase.productId)
                            .get()
                            .await()
                        if (!repairsSnapshot.isEmpty) {
                            val repair = repairsSnapshot.documents.first().toObject(Repair::class.java)
                            purchase.repairStatus = repair?.status
                        }
                        purchase
                    }
                    purchaseHistory = purchases
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }
    }


    fun reportProduct(purchase: Purchase, issueDescription: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            val userName = auth.currentUser?.displayName
            if (userId != null) {
                val repair = Repair(
                    userId = userId,
                    userName = userName ?: "",
                    productId = purchase.productId,
                    productName = purchase.productName,
                    productImageUrl = purchase.imageUrl,
                    issueDescription = issueDescription,
                    status = RepairStatus.PENDING
                )
                db.collection("repairs").add(repair).addOnSuccessListener {
                    loadPurchaseHistory()
                }
            }
        }
    }
}