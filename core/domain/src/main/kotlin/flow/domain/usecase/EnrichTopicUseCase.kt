package flow.domain.usecase

import flow.data.api.BookmarksRepository
import flow.data.api.FavoritesRepository
import flow.data.api.TopicHistoryRepository
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class EnrichTopicUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val topicHistoryRepository: TopicHistoryRepository,
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun <T : Topic> invoke(topic: T): Flow<TopicModel<T>> {
        return combine(
            favoritesRepository.observeIds(),
            favoritesRepository.observeUpdatedIds(),
            topicHistoryRepository.observeIds(),
            topic.category?.id?.let { id ->
                bookmarksRepository.observeNewTopics(id)
            } ?: bookmarksRepository.observeNewTopics(),
        ) { favoriteTopics, updatedIds, visitedTopics, newTopics ->
            TopicModel(
                topic = topic,
                isVisited = visitedTopics.contains(topic.id),
                isFavorite = favoriteTopics.contains(topic.id),
                isNew = newTopics.contains(topic.id),
                hasUpdate = updatedIds.contains(topic.id),
            )
        }
    }
}
