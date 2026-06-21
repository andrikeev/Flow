package flow.network.dto.forum

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String? = null,
    val name: String,
    val children: List<CategoryDto>? = null,
)
