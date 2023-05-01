package flow.favorites

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent

sealed interface FavoritesAction {
    data class TopicClick(val topicModel: TopicModel<out Topic>) : FavoritesAction
}
