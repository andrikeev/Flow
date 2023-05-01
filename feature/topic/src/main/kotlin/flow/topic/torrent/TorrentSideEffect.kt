package flow.topic.torrent

import flow.models.search.Filter

internal sealed interface TorrentSideEffect {
    object Back : TorrentSideEffect
    data class OpenCategory(val id: String) : TorrentSideEffect
    data class OpenComments(val id: String) : TorrentSideEffect
    data class OpenFile(val uri: String) : TorrentSideEffect
    object OpenLogin : TorrentSideEffect
    data class OpenSearch(val filter: Filter) : TorrentSideEffect
    data class ShareLink(val link: String) : TorrentSideEffect
    object ShowDownloadProgress : TorrentSideEffect
    object ShowLoginRequest : TorrentSideEffect
    data class ShowMagnet(val link: String) : TorrentSideEffect
}
