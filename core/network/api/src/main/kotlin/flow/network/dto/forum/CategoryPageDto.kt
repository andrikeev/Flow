package flow.network.dto.forum

import flow.network.dto.topic.ForumTopicDto
import kotlinx.serialization.Serializable

@Serializable
data class CategoryPageDto(
    val category: CategoryDto,
    val page: Int,
    val pages: Int,
    val sections: List<SectionDto>? = null,
    val children: List<CategoryDto>? = null,
    val topics: List<ForumTopicDto>? = null,
)
