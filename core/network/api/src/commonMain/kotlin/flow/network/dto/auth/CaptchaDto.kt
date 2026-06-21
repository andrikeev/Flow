package flow.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class CaptchaDto(
    val id: String,
    val code: String,
    val url: String,
)
