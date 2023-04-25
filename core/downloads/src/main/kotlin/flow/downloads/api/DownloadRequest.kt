package flow.downloads.api

data class DownloadRequest(
    val id: String,
    val title: String,
    val uri: String,
    val headers: Collection<Pair<String, String>>,
)
