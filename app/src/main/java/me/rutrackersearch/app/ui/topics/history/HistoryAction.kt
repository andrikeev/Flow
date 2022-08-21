package me.rutrackersearch.app.ui.topics.history

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface HistoryAction {
    data class TopicClick(val topic: Topic) : HistoryAction
    data class TorrentClick(val torrent: Torrent) : HistoryAction
    data class FavoriteClick(val favorable: TopicModel<Topic>) : HistoryAction
}
