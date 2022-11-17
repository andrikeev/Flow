package flow.domain.usecase

import flow.data.api.SearchHistoryRepository
import flow.data.api.SuggestsRepository
import flow.data.api.TopicHistoryRepository
import javax.inject.Inject

class ClearHistoryUseCase @Inject constructor(
    private val suggestsRepository: SuggestsRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val topicHistoryRepository: TopicHistoryRepository,
) {
    suspend operator fun invoke() {
        suggestsRepository.clear()
        searchHistoryRepository.clear()
        topicHistoryRepository.clear()
    }
}
