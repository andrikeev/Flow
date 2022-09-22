package me.rutrackersearch.app.ui.topics.history

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface HistorySideEffect {
    data class OpenTopic(val topic: Topic) : HistorySideEffect
    data class OpenTorrent(val torrent: Torrent) : HistorySideEffect
}
