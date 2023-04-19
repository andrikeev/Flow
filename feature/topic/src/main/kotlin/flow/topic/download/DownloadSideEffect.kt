package flow.topic.download

sealed interface DownloadSideEffect {
    object Dismiss : DownloadSideEffect
    data class OpenFile(val uri: String) : DownloadSideEffect
    object OpenLogin : DownloadSideEffect
}