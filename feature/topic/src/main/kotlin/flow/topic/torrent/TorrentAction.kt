package flow.topic.torrent

sealed interface TorrentAction {
    object AuthorClick : TorrentAction
    object BackClick : TorrentAction
    object CategoryClick : TorrentAction
    object CommentsClick : TorrentAction
    object FavoriteClick : TorrentAction
    object MagnetClick : TorrentAction
    object RetryClick : TorrentAction
    object ShareClick : TorrentAction
    object TorrentFileClick : TorrentAction
}
