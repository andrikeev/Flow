package flow.domain.usecase

import flow.data.api.repository.SearchHistoryRepository
import flow.models.search.Search
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class ObserveSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository,
) {
    operator fun invoke(): Flow<List<Search>> {
        return repository.observeSearchHistory()
            .distinctUntilChanged()
            .catch {
                repository.clear()
                emit(emptyList())
            }
    }
}
