package flow.domain.usecase

import flow.data.api.repository.FavoriteSearchRepository
import flow.models.search.Search
import javax.inject.Inject

class PinSearchHistoryUseCase @Inject constructor(
    private val repository: FavoriteSearchRepository,
) {
    suspend operator fun invoke(search: Search) {
        repository.add(search.id)
    }
}
