package com.example.techport.data

data class Repair(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val productId: String = "",
    val productName: String = "",
    val productImageUrl: String = "",
    val issueDescription: String = "",
    val issueImageUrls: List<String> = emptyList(),
    val status: RepairStatus = RepairStatus.PENDING,
    val estimatedCost: Double = 0.0,
    val actualCost: Double = 0.0,
    val technicianNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

enum class RepairStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromString(value: String): RepairStatus {
            return entries.find { it.name == value } ?: PENDING
        }
    }
}

// Extension function to convert enum to string for Firestore
fun RepairStatus.toFirestoreString(): String = this.name