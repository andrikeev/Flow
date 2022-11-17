package flow.topics.history

import flow.models.topic.Topic
import flow.models.topic.Torrent

sealed interface HistorySideEffect {
    data class OpenTopic(val topic: Topic) : HistorySideEffect
    data class OpenTorrent(val torrent: Torrent) : HistorySideEffect
}
