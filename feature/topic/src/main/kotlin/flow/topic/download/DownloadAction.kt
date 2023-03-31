package flow.topic.download

sealed interface DownloadAction {
    object Dismiss : DownloadAction
    object Download : DownloadAction
    object LoginClick : DownloadAction
    object OpenFile : DownloadAction
}
