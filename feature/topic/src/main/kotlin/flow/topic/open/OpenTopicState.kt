package flow.topic.open

import flow.models.forum.Category
import flow.models.topic.Author
import flow.models.topic.TorrentDescription
import flow.models.topic.TorrentStatus

internal sealed interface OpenTopicState {
    object Loading : OpenTopicState
    data class Error(val error: Throwable) : OpenTopicState
    data class Topic(val title: String) : OpenTopicState
    data class Torrent(
        val title: String,
        val posterImage: String?,
        val author: Author?,
        val category: Category?,
        val status: TorrentStatus?,
        val date: Long?,
        val size: String?,
        val seeds: Int?,
        val leeches: Int?,
        val magnetLink: String?,
        val description: TorrentDescription?,
        val showMagnetLink: Boolean,
        val showTorrentFile: Boolean,
    ) : OpenTopicState
}
