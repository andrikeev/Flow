package flow.topic.download

sealed interface DownloadDialogState {
    object Initial : DownloadDialogState
    object Unauthorised : DownloadDialogState

    sealed interface DownloadState: DownloadDialogState {
        object Loading : DownloadState
        data class Completed(val uri: String) : DownloadState
    }
}
