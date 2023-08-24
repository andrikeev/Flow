package flow.favorites

import flow.models.topic.Topic
import flow.models.topic.TopicModel

sealed interface FavoritesState {
    data object Initial : FavoritesState
    data object Empty : FavoritesState
    data class FavoritesList(val items: List<TopicModel<out Topic>>) : FavoritesState
}
