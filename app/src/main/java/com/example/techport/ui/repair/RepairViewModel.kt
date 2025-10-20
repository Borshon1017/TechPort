package com.example.techport.ui.repair

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techport.data.Repair
import com.example.techport.data.RepairRepository
import com.example.techport.data.RepairStatus
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RepairViewModel : ViewModel() {
    private val repository = RepairRepository()
    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val crashlytics = FirebaseCrashlytics.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var repairs by mutableStateOf<List<Repair>>(emptyList())
        private set

    var selectedStatusFilter by mutableStateOf<RepairStatus?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadRepairs()
    }

    fun loadRepairs() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = if (selectedStatusFilter != null) {
                    repository.getRepairsByStatus(selectedStatusFilter!!)
                } else {
                    repository.getAllRepairs()
                }

                result.fold(
                    onSuccess = { repairs = it },
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

    fun filterByStatus(status: RepairStatus?) {
        selectedStatusFilter = status
        loadRepairs()
    }

    fun createRepair(
        repair: Repair,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val currentUser = auth.currentUser
            val repairWithUser = repair.copy(
                userId = currentUser?.uid ?: "",
                userName = currentUser?.displayName ?: currentUser?.email ?: "Anonymous"
            )

            repository.createRepair(repairWithUser).fold(
                onSuccess = {
                    analytics.logEvent("repair_created") {
                        param("product_name", repair.productName)
                        param("estimated_cost", repair.estimatedCost)
                    }
                    loadRepairs()
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to create repair request")
                }
            )
            isLoading = false
        }
    }

    fun updateRepair(
        repair: Repair,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            repository.updateRepair(repair).fold(
                onSuccess = {
                    analytics.logEvent("repair_updated") {
                        param("repair_id", repair.id)
                        param("new_status", repair.status.name)
                    }
                    loadRepairs()
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to update repair")
                }
            )
            isLoading = false
        }
    }

    fun updateRepairStatus(
        repairId: String,
        newStatus: RepairStatus,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true

            repository.getRepair(repairId).fold(
                onSuccess = { existingRepair ->
                    if (existingRepair != null) {
                        val updatedRepair = existingRepair.copy(
                            status = newStatus,
                            completedAt = if (newStatus == RepairStatus.COMPLETED)
                                System.currentTimeMillis() else null
                        )
                        updateRepair(updatedRepair, {
                            loadRepairs() // Refresh the list after update
                            onSuccess()
                        }, onError)
                    } else {
                        onError("Repair not found")
                    }
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to fetch repair")
                }
            )
            isLoading = false
        }
    }

    fun deleteRepair(
        repairId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            repository.deleteRepair(repairId).fold(
                onSuccess = {
                    analytics.logEvent("repair_deleted") {
                        param("repair_id", repairId)
                    }
                    loadRepairs() // Refresh the list after delete
                    onSuccess()
                },
                onFailure = {
                    crashlytics.recordException(it)
                    onError(it.message ?: "Failed to delete repair")
                }
            )
            isLoading = false
        }
    }

    fun getRepairById(id: String): Repair? {
        return repairs.find { it.id == id }
    }
}