package flow.network.domain

import flow.network.api.RuTrackerInnerApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.auth.CaptchaDto
import flow.network.dto.auth.UserDto
import flow.network.model.NoData
import java.util.regex.Pattern

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
            } else {
                AuthResponseDto.CaptchaRequired(captcha)
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
        private val codeRegex =
            Pattern.compile("<input[^>]*name=\"(cap_code_[^\"]+)\"[^>]*value=\"[^\"]*\"[^>]*>")
        private val sidRegex =
            Pattern.compile("<input[^>]*name=\"cap_sid\"[^>]*value=\"([^\"]+)\"[^>]*>")
        private val urlRegex = Pattern.compile("<img[^>]*src=\"([^\"]+/captcha/[^\"]+)\"[^>]*>")

        operator fun invoke(from: String): CaptchaDto? {
            val codeMatcher = codeRegex.matcher(from)
            val sidMatcher = sidRegex.matcher(from)
            val urlMatcher = urlRegex.matcher(from)
            return if (codeMatcher.find() && sidMatcher.find() && urlMatcher.find()) {
                val captchaUrl = urlMatcher.group(1).let { url ->
                    url.takeIf { it.contains("http") } ?: "https://${url.trim('/')}"
                }
                CaptchaDto(sidMatcher.group(1), codeMatcher.group(1), captchaUrl)
            } else {
                null
            }
        }
    }
}
