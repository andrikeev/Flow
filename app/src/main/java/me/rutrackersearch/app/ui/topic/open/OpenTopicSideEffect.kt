package me.rutrackersearch.app.ui.topic.open

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface OpenTopicSideEffect {
    object Back : OpenTopicSideEffect
    data class OpenTopic(val topic: Topic) : OpenTopicSideEffect
    data class OpenTorrent(val torrent: Torrent) : OpenTopicSideEffect
}
