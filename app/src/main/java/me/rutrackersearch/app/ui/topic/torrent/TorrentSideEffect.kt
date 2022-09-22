package me.rutrackersearch.app.ui.topic.torrent

import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.Torrent

sealed interface TorrentSideEffect {
    object Back : TorrentSideEffect
    data class Download(val torrent: Torrent) : TorrentSideEffect
    data class OpenCategory(val category: Category) : TorrentSideEffect
    data class OpenSearch(val filter: Filter) : TorrentSideEffect
    data class OpenComments(val topic: Topic) : TorrentSideEffect
    data class OpenMagnet(val magnetLink: String) : TorrentSideEffect
    data class Share(val link: String) : TorrentSideEffect
}
