package flow.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val token: String,
    val avatarUrl: String,
)
