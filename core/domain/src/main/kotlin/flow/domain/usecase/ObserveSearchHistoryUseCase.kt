package flow.domain.usecase

import flow.data.api.SearchHistoryRepository
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository,
) {
    operator fun invoke(): Flow<List<Search>> = repository.observeSearchHistory()
}
