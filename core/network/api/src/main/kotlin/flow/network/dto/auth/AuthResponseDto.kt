package flow.network.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthResponseDto {
    @Serializable
    @SerialName("Success")
    data class Success(val user: UserDto) : AuthResponseDto

    @Serializable
    @SerialName("WrongCredits")
    data class WrongCredits(val captcha: CaptchaDto?) : AuthResponseDto

    @Serializable
    @SerialName("CaptchaRequired")
    data class CaptchaRequired(val captcha: CaptchaDto?) : AuthResponseDto
}
