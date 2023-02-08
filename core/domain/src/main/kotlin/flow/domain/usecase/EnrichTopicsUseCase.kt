package flow.domain.usecase

import flow.data.api.repository.BookmarksRepository
import flow.data.api.repository.FavoritesRepository
import flow.data.api.repository.VisitedRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class EnrichTopicsUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val favoritesRepository: FavoritesRepository,
    private val visitedRepository: VisitedRepository,
) {
    operator fun <T : Topic> invoke(topics: List<T>): Flow<List<TopicModel<T>>> {
        return combine(
            favoritesRepository.observeIds(),
            favoritesRepository.observeUpdatedIds(),
            visitedRepository.observeIds(),
            bookmarksRepository.observeNewTopics(),
        ) { favoriteTopics, updatedTopics, visitedTopics, newTopics ->
            topics.map { topic ->
                TopicModel(
                    topic = topic,
                    isVisited = visitedTopics.contains(topic.id),
                    isFavorite = favoriteTopics.contains(topic.id),
                    isNew = newTopics.contains(topic.id),
                    hasUpdate = updatedTopics.contains(topic.id),
                )
            }
        }
    }
}
