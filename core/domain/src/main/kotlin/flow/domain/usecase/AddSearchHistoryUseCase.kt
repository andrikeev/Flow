package flow.domain.usecase

import flow.data.api.SearchHistoryRepository
import flow.models.search.Filter
import javax.inject.Inject

class AddSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository,
) {
    suspend operator fun invoke(filter: Filter) {
        repository.addSearch(filter)
    }
}
