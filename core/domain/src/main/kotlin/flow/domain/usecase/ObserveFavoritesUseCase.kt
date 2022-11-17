package flow.domain.usecase

import flow.data.api.FavoritesRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<TopicModel<out Topic>>> = favoritesRepository.observeTopics()
}
