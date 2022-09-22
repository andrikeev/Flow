package me.rutrackersearch.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.rutrackersearch.auth.models.AccountData
import me.rutrackersearch.auth.models.AuthResponse
import me.rutrackersearch.auth.models.Captcha
import me.rutrackersearch.network.HostProvider
import me.rutrackersearch.network.utils.getIdFromUrl
import me.rutrackersearch.network.utils.getString
import me.rutrackersearch.network.utils.post
import me.rutrackersearch.network.utils.request
import me.rutrackersearch.network.utils.url
import me.rutrackersearch.network.utils.urlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.util.regex.Pattern
import javax.inject.Inject

class AuthServiceImpl @Inject constructor(
    private val hostProvider: HostProvider,
) : AuthService {
    private val httpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
        .build()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?
    ): AuthResponse {
        return withContext(Dispatchers.IO) {
            runCatching {
                val loginResponse = httpClient.request {
                    url(host = hostProvider.host, path = "login.php")
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
                if (token != null) {
                    val indexData = httpClient.request {
                        url(host = hostProvider.host, path = "index.php")
                        header("Cookie", token)
                        get()
                    }.getString()
                    val userId = parseUserId(indexData)
                    val profileData = httpClient.request {
                        url(host = hostProvider.host, path = "login.php", "mode" to "viewprofile", "u" to userId)
                        header("Cookie", token)
                        get()
                    }.getString()
                    val avatarUrl = parseAvatarUrl(profileData)
                    AuthResponse.Success(AccountData(userId, token, avatarUrl))
                } else if (loginData.contains(LOGIN_FORM_KEY)) {
                    val captcha = parseCaptcha(loginData)
                    if (loginData.contains(WRONG_CREDITS_MESSAGE)) {
                        AuthResponse.WrongCredits(captcha)
                    } else if (captcha != null) {
                        AuthResponse.CaptchaRequired(captcha)
                    } else {
                        AuthResponse.Error(RuntimeException())
                    }
                } else {
                    AuthResponse.Error(RuntimeException())
                }
            }
                .fold(
                    onSuccess = { it },
                    onFailure = AuthResponse::Error,
                )
        }
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
