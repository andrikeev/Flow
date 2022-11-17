package flow.domain.usecase

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: flow.data.api.FavoritesRepository,
) {
    suspend operator fun <T : Topic> invoke(topic: TopicModel<T>) {
        if (topic.isFavorite) {
            favoritesRepository.remove(topic.topic)
        } else {
            favoritesRepository.add(topic.topic)
        }
    }
}
