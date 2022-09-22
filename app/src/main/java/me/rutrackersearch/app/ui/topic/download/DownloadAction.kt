package me.rutrackersearch.app.ui.topic.download

sealed interface DownloadAction {
    object Dismiss : DownloadAction
    object Download : DownloadAction
    object LoginClick : DownloadAction
    object OpenFile : DownloadAction
    object SettingsClick : DownloadAction
}
