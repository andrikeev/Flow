package me.rutrackersearch.domain.usecase

import me.rutrackersearch.domain.repository.SearchHistoryRepository
import me.rutrackersearch.domain.repository.SuggestsRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
