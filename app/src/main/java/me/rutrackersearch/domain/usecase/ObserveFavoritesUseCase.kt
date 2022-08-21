package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.FavoritesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ObserveFavoritesUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) {
    operator fun invoke(): Flow<List<TopicModel<out Topic>>> {
        return favoritesRepository.observeTopics()
    }
}
