package flow.favorites

import flow.models.topic.Topic
import flow.models.topic.Torrent

sealed interface FavoritesAction {
    data class TopicClick(val topics: Topic) : FavoritesAction
    data class TorrentClick(val torrent: Torrent) : FavoritesAction
}
