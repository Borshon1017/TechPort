package com.example.techport.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    // Fake Store API - External API for demonstration
    private val baseUrl = "https://fakestoreapi.com"

    suspend fun fetchExternalProducts(): List<ExternalProduct> {
        return try {
            val response: List<ExternalProduct> = client.get("$baseUrl/products").body()
            response
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchProductsByCategory(category: String): List<ExternalProduct> {
        return try {
            val response: List<ExternalProduct> = client.get("$baseUrl/products/category/$category").body()
            response
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun close() {
        client.close()
    }
}