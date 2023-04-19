package flow.topic.torrent

import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.Torrent

sealed interface TorrentSideEffect {
    object Back : TorrentSideEffect
    data class Download(val torrent: Torrent) : TorrentSideEffect
    data class OpenCategory(val categoryId: String) : TorrentSideEffect
    data class OpenSearch(val filter: Filter) : TorrentSideEffect
    data class OpenComments(val topic: Topic) : TorrentSideEffect
    data class OpenMagnet(val magnetLink: String) : TorrentSideEffect
    data class Share(val link: String) : TorrentSideEffect
}
