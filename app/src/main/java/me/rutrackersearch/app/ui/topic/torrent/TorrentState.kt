package me.rutrackersearch.app.ui.topic.torrent

import android.net.Uri
import me.rutrackersearch.domain.entity.TopicModel
import me.rutrackersearch.domain.entity.topic.Torrent

sealed interface TorrentState {
    val data: TopicModel<Torrent>
    val torrentFile: Uri?

    data class Loading(
        override val data: TopicModel<Torrent>,
        override val torrentFile: Uri?,
    ) : TorrentState

    data class Loaded(
        override val data: TopicModel<Torrent>,
        override val torrentFile: Uri?,
    ) : TorrentState

    data class Error(
        override val data: TopicModel<Torrent>,
        override val torrentFile: Uri?,
        val error: Throwable,
    ) : TorrentState
}
