package flow.auth.impl

import flow.auth.api.AuthService
import flow.auth.models.AuthResponse
import flow.auth.models.Captcha
import flow.dispatchers.api.Dispatchers
import flow.logger.api.LoggerFactory
import flow.models.user.Account
import flow.networkutils.getIdFromUrl
import flow.networkutils.getString
import flow.networkutils.post
import flow.networkutils.request
import flow.networkutils.url
import flow.networkutils.urlOrNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Named

internal class AuthServiceImpl @Inject constructor(
    @Named("auth") private val httpClient: OkHttpClient,
    private val coroutineDispatchers: Dispatchers,
    loggerFactory: LoggerFactory,
) : AuthService {
    private val logger = loggerFactory.get("AuthServiceImpl")

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResponse = coroutineScope {
        runCatching {
            withContext(coroutineDispatchers.io) {
                val loginResponse = httpClient.request {
                    url(path = "login.php")
                    post {
                        addEncoded("login_username", username.toCp1251())
                        addEncoded("login_password", password.toCp1251())
                        addEncoded("login", "Вход")
                        if (captchaSid != null && captchaCode != null && captchaValue != null) {
                            addEncoded("cap_sid", captchaSid)
                            addEncoded(captchaCode, captchaValue)
                        }
                    }
                }
                val token = loginResponse.priorResponse?.header("Set-Cookie")
                val loginData = loginResponse.getString()
                when {
                    !token.isNullOrEmpty() -> {
                        val id = getUserId(token)
                        val avatarUrl = getUserAvatarUrl(id, token)
                        AuthResponse.Success(Account(id, username, password, token, avatarUrl))
                    }

                    loginData.contains(LOGIN_FORM_KEY) -> {
                        val captcha = parseCaptcha(loginData)
                        when {
                            loginData.contains(WRONG_CREDITS_MESSAGE) -> AuthResponse.WrongCredits(captcha)
                            captcha != null -> AuthResponse.CaptchaRequired(captcha)
                            else -> AuthResponse.Error(RuntimeException())
                        }
                    }

                    else -> AuthResponse.Error(RuntimeException())
                }
            }
        }
            .onFailure { error -> logger.e(error) { "Login error" } }
            .getOrElse(AuthResponse::Error)
    }

    private suspend fun getUserId(token: String): String {
        val indexData = httpClient.request {
            url(path = "index.php")
            header("Cookie", token)
            get()
        }.getString()
        return parseUserId(indexData)
    }

    private suspend fun getUserAvatarUrl(id: String, token: String): String? {
        val profileData = httpClient.request {
            url(path = "login.php", "mode" to "viewprofile", "u" to id)
            header("Cookie", token)
            get()
        }.getString()
        return parseAvatarUrl(profileData)
    }

    private fun parseUserId(data: String) = Jsoup.parse(data).let { doc ->
        val userProfileUrl = requireNotNull(doc.select("#logged-in-username").urlOrNull()) { "profile url is null" }
        requireNotNull(getIdFromUrl(userProfileUrl, "u")) { "user id not found" }
    }

    private fun parseAvatarUrl(data: String) = Jsoup.parse(data).select("#avatar-img > img").attr("src")

    private fun parseCaptcha(data: String): Captcha? {
        val codeMatcher = CAP_CODE_REGEX.matcher(data)
        val sidMatcher = CAP_SID_REGEX.matcher(data)
        val urlMatcher = CAP_SRC_REGEX.matcher(data)

        val code = if (codeMatcher.find()) codeMatcher.group(1) else null
        val sid = if (sidMatcher.find()) sidMatcher.group(1) else null
        val url = if (urlMatcher.find()) urlMatcher.group(1) else null

        return if (code != null && sid != null && url != null) {
            val captchaUrl = url.takeIf { it.contains("http") }
                ?: "http://${url.trim('/')}"
            Captcha(sid, code, captchaUrl)
        } else {
            null
        }
    }

    private fun String.toCp1251(): String = URLEncoder.encode(this, "Windows-1251")

    companion object {
        private val CAP_CODE_REGEX = Pattern.compile("<input[^>]*name=\"(cap_code_[^\"]+)\"[^>]*value=\"[^\"]*\"[^>]*>")
        private val CAP_SID_REGEX = Pattern.compile("<input[^>]*name=\"cap_sid\"[^>]*value=\"([^\"]+)\"[^>]*>")
        private val CAP_SRC_REGEX = Pattern.compile("<img[^>]*src=\"([^\"]+/captcha/[^\"]+)\"[^>]*>")
        private const val LOGIN_FORM_KEY = "login-form"
        private const val WRONG_CREDITS_MESSAGE = "неверный пароль"
    }
}
