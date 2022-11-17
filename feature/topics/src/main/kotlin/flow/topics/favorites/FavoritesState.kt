package flow.topics.favorites

import flow.models.topic.Topic
import flow.models.topic.TopicModel

sealed interface FavoritesState {
    object Initial : FavoritesState
    object Empty : FavoritesState
    data class FavoritesList(val items: List<TopicModel<out Topic>>) : FavoritesState
}
