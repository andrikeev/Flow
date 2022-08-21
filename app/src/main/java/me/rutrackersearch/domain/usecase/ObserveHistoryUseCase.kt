package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveHistoryUseCase @Inject constructor(
    private val repository: TopicHistoryRepository,
) {
    operator fun invoke(): Flow<List<Topic>> = repository.observeTopics()
}
