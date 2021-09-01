package me.rutrackersearch.app.ui.topics.history

import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Topic
import me.rutrackersearch.domain.entity.topic.Torrent

sealed interface HistoryAction {
    data class TopicClick(val topic: Topic) : HistoryAction
    data class TorrentClick(val torrent: Torrent) : HistoryAction
    data class FavoriteClick(val favorable: TopicModel<Topic>) : HistoryAction
}
