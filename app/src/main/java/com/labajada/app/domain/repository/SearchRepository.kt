package com.labajada.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun saveSearchQuery(buyerId: String, query: String)
    fun getSearchHistory(buyerId: String): Flow<List<String>>
    suspend fun clearHistory(buyerId: String)
    suspend fun deleteSearchQuery(buyerId: String, query: String)
}