package flow.domain.usecase

import flow.data.api.repository.FavoriteSearchRepository
import flow.models.search.Search

class PinSearchHistoryUseCase(
    private val repository: FavoriteSearchRepository,
) {
    suspend operator fun invoke(search: Search) {
        repository.add(search.id)
    }
}
