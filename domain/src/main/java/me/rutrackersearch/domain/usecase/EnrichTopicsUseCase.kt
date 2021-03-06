package me.rutrackersearch.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.repository.BookmarksRepository
import me.rutrackersearch.domain.repository.FavoritesRepository
import me.rutrackersearch.domain.repository.TopicHistoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnrichTopicsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
    private val topicHistoryRepository: TopicHistoryRepository,
    private val bookmarksRepository: BookmarksRepository,
) {
    operator fun <T : Topic> invoke(topics: List<T>): Flow<List<TopicModel<T>>> {
        return combine(
            favoritesRepository.observeIds(),
            favoritesRepository.observeUpdatedIds(),
            topicHistoryRepository.observeIds(),
            bookmarksRepository.observeNewTopics(),
        ) { favoriteTopics, updatedTopics, visitedTopics, newTopics ->
            topics.map { topic ->
                TopicModel(
                    data = topic,
                    isVisited = visitedTopics.contains(topic.id),
                    isFavorite = favoriteTopics.contains(topic.id),
                    isNew = newTopics.contains(topic.id),
                    hasUpdate = updatedTopics.contains(topic.id),
                )
            }
        }
    }
}
