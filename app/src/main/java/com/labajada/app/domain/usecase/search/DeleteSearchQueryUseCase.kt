package com.labajada.app.domain.usecase.search

import com.labajada.app.domain.repository.SearchRepository

class DeleteSearchQueryUseCase(private val repository: SearchRepository) {
    suspend operator fun invoke(buyerId: String, query: String) {
        repository.deleteSearchQuery(buyerId, query)
    }
}
