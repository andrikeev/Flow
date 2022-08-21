package me.rutrackersearch.domain.usecase

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.FavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    suspend operator fun <T : Topic> invoke(topic: TopicModel<T>) {
        if (topic.isFavorite) {
            favoritesRepository.remove(topic.data)
        } else {
            favoritesRepository.add(topic.data)
        }
    }
}
