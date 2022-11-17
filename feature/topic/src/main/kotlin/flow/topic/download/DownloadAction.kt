package flow.topic.download

sealed interface DownloadAction {
    object Dismiss : flow.topic.download.DownloadAction
    object Download : flow.topic.download.DownloadAction
    object LoginClick : flow.topic.download.DownloadAction
    object OpenFile : flow.topic.download.DownloadAction
    object SettingsClick : flow.topic.download.DownloadAction
}
