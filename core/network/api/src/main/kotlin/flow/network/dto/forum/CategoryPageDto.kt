package flow.network.dto.forum

import flow.network.dto.topic.ForumTopicDto
import kotlinx.serialization.Serializable

@Serializable
data class CategoryPageDto(
    val category: CategoryDto,
    val page: Int,
    val pages: Int,
    val children: List<CategoryDto>?,
    val topics: List<ForumTopicDto>?,
)
