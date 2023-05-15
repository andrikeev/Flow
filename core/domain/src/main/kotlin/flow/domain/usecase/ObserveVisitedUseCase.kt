package flow.domain.usecase

import flow.data.api.repository.VisitedRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class ObserveVisitedUseCase @Inject constructor(
    private val visitedRepository: VisitedRepository,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
) {
    operator fun invoke(): Flow<List<TopicModel<out Topic>>> {
        return visitedRepository.observeTopics()
            .flatMapLatest(enrichTopicsUseCase::invoke)
            .distinctUntilChanged()
            .catch {
                visitedRepository.clear()
                emit(emptyList())
            }
    }
}
