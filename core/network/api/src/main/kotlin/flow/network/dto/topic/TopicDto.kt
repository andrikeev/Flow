package flow.network.dto.topic

import flow.network.dto.forum.CategoryDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface ForumTopicDto {
    val id: String
    val title: String
    val author: AuthorDto?
    val category: CategoryDto?
}

@Serializable
@SerialName("Topic")
data class TopicDto(
    override val id: String,
    override val title: String,
    override val author: AuthorDto? = null,
    override val category: CategoryDto? = null,
) : ForumTopicDto

@Serializable
@SerialName("Torrent")
data class TorrentDto(
    override val id: String,
    override val title: String,
    override val author: AuthorDto? = null,
    override val category: CategoryDto? = null,
    val tags: String? = null,
    val status: TorrentStatusDto? = null,
    val date: Long? = null,
    val size: String? = null,
    val seeds: Int? = null,
    val leeches: Int? = null,
    val magnetLink: String? = null,
    val description: TorrentDescriptionDto? = null,
) : ForumTopicDto

@Serializable
@SerialName("CommentsPage")
data class CommentsPageDto(
    override val id: String,
    override val title: String,
    override val author: AuthorDto? = null,
    override val category: CategoryDto? = null,
    val page: Int,
    val pages: Int,
    val posts: List<PostDto>,
) : ForumTopicDto
