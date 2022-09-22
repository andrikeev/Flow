package me.rutrackersearch.app.ui.topics.favorites

import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface FavoritesAction {
    data class TopicClick(val topics: Topic) : FavoritesAction
    data class TorrentClick(val torrent: Torrent) : FavoritesAction
}