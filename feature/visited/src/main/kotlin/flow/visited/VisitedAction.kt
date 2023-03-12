package flow.visited

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent

internal sealed interface VisitedAction {
    data class TopicClick(val topic: Topic) : VisitedAction
    data class TorrentClick(val torrent: Torrent) : VisitedAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : VisitedAction
}
