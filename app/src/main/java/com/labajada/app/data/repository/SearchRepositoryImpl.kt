package com.labajada.app.data.repository

import com.labajada.app.data.local.dao.SearchDao
import com.labajada.app.data.local.entity.SearchHistoryEntity
import com.labajada.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepositoryImpl(
    private val searchDao: SearchDao
) : SearchRepository {

    override suspend fun saveSearchQuery(buyerId: String, query: String) {
        searchDao.insertSearchQuery(
            SearchHistoryEntity(
                buyerId = buyerId,
                searchQuery = query,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override fun getSearchHistory(buyerId: String): Flow<List<String>> {
        return searchDao.getRecentSearchHistory(buyerId).map { entities ->
            entities.map { it.searchQuery }
        }
    }

    override suspend fun clearHistory(buyerId: String) {
        searchDao.clearAllSearchHistory(buyerId)
    }

    override suspend fun deleteSearchQuery(buyerId: String, query: String) {
        searchDao.deleteSearchQuery(buyerId, query)
    }
}