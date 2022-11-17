package flow.topic.torrent

import flow.models.topic.TopicModel
import flow.models.topic.Torrent

data class TorrentState(
    val torrent: TopicModel<Torrent>,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val isAuthorised: Boolean = false,
)
