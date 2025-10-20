package com.example.techport.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class RepairRepository {
    private val db = FirebaseFirestore.getInstance()
    private val repairsCollection = db.collection("repairs")

    private fun docToRepair(doc: DocumentSnapshot): Repair? {
        return try {
            val issueImageUrlsRaw = doc.get("issueImageUrls")
            val urls = if (issueImageUrlsRaw is List<*>) {
                issueImageUrlsRaw.mapNotNull { it as? String }
            } else {
                emptyList()
            }
            Repair(
                id = doc.id,
                userId = doc.getString("userId") ?: "",
                userName = doc.getString("userName") ?: "",
                productId = doc.getString("productId") ?: "",
                productName = doc.getString("productName") ?: "",
                productImageUrl = doc.getString("productImageUrl") ?: "",
                issueDescription = doc.getString("issueDescription") ?: "",
                issueImageUrls = urls,
                status = RepairStatus.fromString(doc.getString("status") ?: "PENDING"),
                estimatedCost = doc.getDouble("estimatedCost") ?: 0.0,
                actualCost = doc.getDouble("actualCost") ?: 0.0,
                technicianNotes = doc.getString("technicianNotes") ?: "",
                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                completedAt = doc.getLong("completedAt")
            )
        } catch (e: Exception) {
            null
        }
    }

    // Create
    suspend fun createRepair(repair: Repair): Result<String> {
        return try {
            val docRef = repairsCollection.document()
            val repairWithId = repair.copy(id = docRef.id)

            val repairData = hashMapOf(
                "userId" to repairWithId.userId,
                "userName" to repairWithId.userName,
                "productId" to repairWithId.productId,
                "productName" to repairWithId.productName,
                "productImageUrl" to repairWithId.productImageUrl,
                "issueDescription" to repairWithId.issueDescription,
                "issueImageUrls" to repairWithId.issueImageUrls,
                "status" to repairWithId.status.name,
                "estimatedCost" to repairWithId.estimatedCost,
                "actualCost" to repairWithId.actualCost,
                "technicianNotes" to repairWithId.technicianNotes,
                "createdAt" to repairWithId.createdAt,
                "updatedAt" to repairWithId.updatedAt,
                "completedAt" to repairWithId.completedAt
            )

            docRef.set(repairData).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get all repairs
    suspend fun getAllRepairs(): Result<List<Repair>> {
        return try {
            val snapshot = repairsCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val repairs = snapshot.documents.mapNotNull { doc -> docToRepair(doc) }
            Result.success(repairs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get repairs by user
    suspend fun getRepairsByUser(userId: String): Result<List<Repair>> {
        return try {
            val snapshot = repairsCollection
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val repairs = snapshot.documents.mapNotNull { doc -> docToRepair(doc) }
            Result.success(repairs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get repairs by status
    suspend fun getRepairsByStatus(status: RepairStatus): Result<List<Repair>> {
        return try {
            val snapshot = repairsCollection
                .whereEqualTo("status", status.name)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            val repairs = snapshot.documents.mapNotNull { doc -> docToRepair(doc) }
            Result.success(repairs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Read - Get single repair
    suspend fun getRepair(id: String): Result<Repair?> {
        return try {
            val doc = repairsCollection.document(id).get().await()
            val repair = if (doc.exists()) docToRepair(doc) else null
            Result.success(repair)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update
    suspend fun updateRepair(repair: Repair): Result<Unit> {
        return try {
            val updatedRepair = repair.copy(updatedAt = System.currentTimeMillis())

            val repairData = mapOf(
                "status" to updatedRepair.status.name,
                "estimatedCost" to updatedRepair.estimatedCost,
                "actualCost" to updatedRepair.actualCost,
                "technicianNotes" to updatedRepair.technicianNotes,
                "updatedAt" to updatedRepair.updatedAt,
                "completedAt" to updatedRepair.completedAt
            )

            repairsCollection.document(repair.id).update(repairData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Delete
    suspend fun deleteRepair(id: String): Result<Unit> {
        return try {
            repairsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
