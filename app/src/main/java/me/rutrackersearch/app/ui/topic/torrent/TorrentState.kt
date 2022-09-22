package me.rutrackersearch.app.ui.topic.torrent

import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent

data class TorrentState(
    val torrent: TopicModel<Torrent>,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isAuthorised: Boolean = false,
)
