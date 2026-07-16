package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow

class GetRecentSearchHistoryUseCase(private val repository: SearchRepository) {
    operator fun invoke(buyerId: String): Flow<List<String>> {
        return repository.getSearchHistory(buyerId)
    }
}