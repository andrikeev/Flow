package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
data class PostDto(
    val id: String,
    val author: AuthorDto,
    val date: String,
    val children: List<PostElementDto>
)