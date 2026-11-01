package flow.network.domain

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import flow.network.api.RuTrackerInnerApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.auth.CaptchaDto
import flow.network.dto.auth.UserDto
import flow.network.model.NoData
import flow.network.model.Unknown
import java.net.URI

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
        } else {
            val doc = Ksoup.parse(html)
            val captcha = ParseCaptchaUseCase(doc)
            if (doc.text().contains(WrongCreditsMessage, ignoreCase = true)) {
                AuthResponseDto.WrongCredits(captcha)
            } else if (captcha != null) {
                AuthResponseDto.CaptchaRequired(captcha)
            } else if (doc.select("#login-form, .login-form, input[name=login_username]").isNotEmpty()) {
                throw Unknown
            } else {
                throw NoData
            }
        }
    }

    private companion object {
        const val WrongCreditsMessage = "неверный пароль"
    }

    private object ParseCaptchaUseCase {
        operator fun invoke(doc: Document): CaptchaDto? {
            val sid = doc.select("input[name=cap_sid]").attr("value")
            val code = doc.select("input[name^=cap_code_]").attr("name")
            val src = doc.select("img[src*=/captcha/]").attr("src")
            if (sid.isBlank() || code.isBlank() || src.isBlank()) return null
            val url = runCatching { URI("https://rutracker.org/forum/login.php").resolve(src) }
                .getOrNull() ?: return null
            if (url.scheme !in listOf("http", "https") || url.host.isNullOrBlank()) return null
            return CaptchaDto(sid, code, url.toString())
        }
    }
}
