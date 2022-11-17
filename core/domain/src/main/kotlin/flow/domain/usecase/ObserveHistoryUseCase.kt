package flow.domain.usecase

import flow.data.api.TopicHistoryRepository
import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHistoryUseCase @Inject constructor(
    private val repository: TopicHistoryRepository,
) {
    operator fun invoke(): Flow<List<Topic>> = repository.observeTopics()
}
