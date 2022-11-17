package flow.topics.favorites

import flow.models.topic.Topic
import flow.models.topic.Torrent

sealed interface FavoritesSideEffect {
    data class OpenTopic(val topic: Topic) : FavoritesSideEffect
    data class OpenTorrent(val torrent: Torrent) : FavoritesSideEffect
}
