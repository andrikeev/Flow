package flow.topic.download

sealed interface DownloadState {
    data class Completed(val uri: String) : DownloadState
    object Initial : DownloadState
    object Loading : DownloadState
    object Unauthorised : DownloadState
}
