package me.rutrackersearch.app.ui.topics.favorites

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic

sealed interface FavoritesState {
    object Initial : FavoritesState
    object Empty : FavoritesState
    data class FavoritesList(val items: List<TopicModel<out Topic>>) : FavoritesState
}
