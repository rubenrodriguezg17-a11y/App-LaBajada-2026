package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.repository.SearchRepository

class ClearSearchHistoryUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(buyerId: String) {
        repository.clearHistory(buyerId)
    }
}