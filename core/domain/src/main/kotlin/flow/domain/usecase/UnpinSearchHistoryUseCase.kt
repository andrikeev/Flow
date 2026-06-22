package flow.domain.usecase

import flow.data.api.repository.FavoriteSearchRepository
import flow.models.search.Search

class UnpinSearchHistoryUseCase(
    private val repository: FavoriteSearchRepository,
) {
    suspend operator fun invoke(search: Search) {
        repository.remove(search.id)
    }
}
