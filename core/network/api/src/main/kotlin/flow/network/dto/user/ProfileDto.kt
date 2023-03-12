package flow.network.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(val id: String, val name: String, val avatarUrl: String)