package com.example.techport.ui.history

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

data class Purchase(
    val productId: String = "",
    val productName: String = "",
    val timestamp: Long = 0
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
                db.collection("users").document(userId).collection("purchases")
                    .orderBy("timestamp")
                    .get()
                    .addOnSuccessListener { documents ->
                        purchaseHistory = documents.map { it.toObject(Purchase::class.java) }
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            } else {
                isLoading = false
            }
        }
    }
}