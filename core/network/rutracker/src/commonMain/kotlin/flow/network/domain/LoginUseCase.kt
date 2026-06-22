package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.auth.CaptchaDto
import flow.network.dto.auth.UserDto
import flow.network.model.NoData
import flow.network.model.Unknown

internal class LoginUseCase(
    private val api: RuTrackerInnerApi,
    private val getCurrentProfileUseCase: GetCurrentProfileUseCase,
) {

    suspend operator fun invoke(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResponseDto {
        val (token, html) = api.login(
            username,
            password,
            captchaSid,
            captchaCode,
            captchaValue,
        )
        return if (token != null) {
            val (userId, _, avatarUrl) = getCurrentProfileUseCase(token)
            AuthResponseDto.Success(UserDto(userId, token, avatarUrl))
        } else if (html.contains(LoginFormKey)) {
            val captcha = ParseCaptchaUseCase(html)
            if (html.contains(WrongCreditsMessage)) {
                AuthResponseDto.WrongCredits(captcha)
            } else if (captcha != null) {
                AuthResponseDto.CaptchaRequired(captcha)
            } else {
                throw Unknown
            }
        } else {
            throw NoData
        }
    }

    private companion object {

        const val LoginFormKey = "login-form"
        const val WrongCreditsMessage = "неверный пароль"
    }

    private object ParseCaptchaUseCase {
        private val codeRegex = Regex("<input[^>]*name=\"(cap_code_[^\"]+)\"[^>]*>")
        private val sidRegex = Regex("<input[^>]*name=\"cap_sid\"[^>]*value=\"([^\"]+)\"[^>]*>")
        private val urlRegex = Regex("<img[^>]*src=\"([^\"]+/captcha/[^\"]+)\"[^>]*>")

        operator fun invoke(from: String): CaptchaDto? {
            val codeMatch = codeRegex.find(from)
            val sidMatch = sidRegex.find(from)
            val urlMatch = urlRegex.find(from)
            return if (codeMatch != null && sidMatch != null && urlMatch != null) {
                val captchaUrl = urlMatch.groupValues[1].let { url ->
                    url.takeIf { it.contains("http") } ?: "https://${url.trim('/')}"
                }
                CaptchaDto(sidMatch.groupValues[1], codeMatch.groupValues[1], captchaUrl)
            } else {
                null
            }
        }
    }
}
