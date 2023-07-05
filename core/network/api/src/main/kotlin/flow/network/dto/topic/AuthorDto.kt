package flow.network.dto.topic

import kotlinx.serialization.Serializable

@Serializable
data class AuthorDto(
    val id: String? = null,
    val name: String,
    val avatarUrl: String? = null,
)
