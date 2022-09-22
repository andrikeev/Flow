package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
