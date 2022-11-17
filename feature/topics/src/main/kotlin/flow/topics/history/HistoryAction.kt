package flow.topics.history

import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent

sealed interface HistoryAction {
    data class TopicClick(val topic: Topic) : HistoryAction
    data class TorrentClick(val torrent: Torrent) : HistoryAction
    data class FavoriteClick(val topicModel: TopicModel<out Topic>) : HistoryAction
}
