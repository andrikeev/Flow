package flow.network.dto.forum

import kotlinx.serialization.Serializable

@Serializable
data class ForumDto(val children: List<CategoryDto>)
