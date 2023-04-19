package flow.domain.usecase

import flow.data.api.repository.SearchHistoryRepository
import flow.data.api.repository.SuggestsRepository
import flow.data.api.repository.VisitedRepository
import flow.dispatchers.api.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val visitedRepository: VisitedRepository,
    private val dispatchers: Dispatchers,
) {
    suspend operator fun invoke() {
        withContext(dispatchers.default) {
            suggestsRepository.clear()
            searchHistoryRepository.clear()
            visitedRepository.clear()
        }
    }
}
