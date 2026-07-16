package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.repository.SearchRepository

class SaveSearchQueryUseCase(private val repository: SearchRepository) {

    suspend operator fun invoke(buyerId: String, queryText: String) {
        val cleanQuery = queryText.trim()
        if (cleanQuery.isNotBlank() && cleanQuery.length >= 2) {
            repository.saveSearchQuery(buyerId, cleanQuery)
        }
    }
}