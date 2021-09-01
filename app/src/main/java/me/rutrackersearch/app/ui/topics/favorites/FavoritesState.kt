package me.rutrackersearch.app.ui.topics.favorites

import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic

sealed interface FavoritesState {
    object Initial : FavoritesState
    object Empty : FavoritesState
    data class FavoritesList(val items: List<TopicModel<out Topic>>) : FavoritesState
}
