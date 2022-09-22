package me.rutrackersearch.app.ui.topics.favorites

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface FavoritesSideEffect {
    data class OpenTopic(val topic: Topic) : FavoritesSideEffect
    data class OpenTorrent(val torrent: Torrent) : FavoritesSideEffect
}