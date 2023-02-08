package flow.domain.usecase

import flow.data.api.repository.VisitedRepository
import flow.models.topic.Topic
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveVisitedUseCase @Inject constructor(
    private val visitedRepository: VisitedRepository,
) {
    operator fun invoke(): Flow<List<Topic>> = visitedRepository.observeTopics()
}
