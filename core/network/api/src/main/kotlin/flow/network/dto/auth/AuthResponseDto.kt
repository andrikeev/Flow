package flow.network.dto.auth

import kotlinx.serialization.Serializable

@Serializable
sealed class AuthResponseDto(val status: AuthStatusDto) {

    data class Success(val user: UserDto) : AuthResponseDto(AuthStatusDto.OK)

    data class WrongCredits(val captcha: CaptchaDto?) : AuthResponseDto(AuthStatusDto.WRONG_CREDITS)

    data class CaptchaRequired(val captcha: CaptchaDto?) : AuthResponseDto(AuthStatusDto.CAPTCHA)
}
