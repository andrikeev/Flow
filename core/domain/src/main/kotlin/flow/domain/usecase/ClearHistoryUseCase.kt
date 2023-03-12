package flow.domain.usecase

import flow.data.api.repository.VisitedRepository
import flow.data.api.repository.SearchHistoryRepository
import flow.data.api.repository.SuggestsRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val visitedRepository: VisitedRepository,
) {
    suspend operator fun invoke() {
        suggestsRepository.clear()
        searchHistoryRepository.clear()
        visitedRepository.clear()
    }
}
