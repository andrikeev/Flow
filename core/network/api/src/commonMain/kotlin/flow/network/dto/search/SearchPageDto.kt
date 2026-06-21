package flow.network.dto.search

import flow.network.dto.topic.TorrentDto
import kotlinx.serialization.Serializable

@Serializable
data class SearchPageDto(
    val page: Int,
    val pages: Int,
    val torrents: List<TorrentDto>,
)
