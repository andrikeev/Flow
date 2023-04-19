package flow.topic.torrent

sealed interface TorrentAction {
    object BackClick : TorrentAction
    object RetryClick : TorrentAction
    object CommentsClick : TorrentAction
    object FavoriteClick : TorrentAction
    object ShareClick : TorrentAction
    object MagnetClick : TorrentAction
    object TorrentFileClick : TorrentAction
    object CategoryClick : TorrentAction
    object AuthorClick : TorrentAction
}
