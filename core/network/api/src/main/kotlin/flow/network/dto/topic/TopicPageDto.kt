package flow.network.dto.topic

import flow.network.dto.forum.CategoryDto
import kotlinx.serialization.Serializable

@Serializable
data class TopicPageDto(
    val id: String,
    val title: String,
    val author: AuthorDto?,
    val category: CategoryDto?,
    val torrentData: TorrentDataDto?,
    val commentsPage: TopicPageCommentsDto,
)

@Serializable
data class TorrentDataDto(
    val posterUrl: String? = null,
    val status: TorrentStatusDto? = null,
    val date: String? = null,
    val size: String? = null,
    val seeds: Int? = null,
    val leeches: Int? = null,
    val magnetLink: String? = null,
)

@Serializable
data class TopicPageCommentsDto(
    val page: Int,
    val pages: Int,
    val posts: List<PostDto>,
)
