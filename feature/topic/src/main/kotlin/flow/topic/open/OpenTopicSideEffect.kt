package flow.topic.open

import flow.models.topic.Topic
import flow.models.topic.Torrent

sealed interface OpenTopicSideEffect {
    object Back : OpenTopicSideEffect
    data class OpenTopic(val topic: Topic) : OpenTopicSideEffect
    data class OpenTorrent(val torrent: Torrent) : OpenTopicSideEffect
}
