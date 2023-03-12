package flow.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
enum class AuthStatusDto {
    OK,
    CAPTCHA,
    WRONG_CREDITS,
}
