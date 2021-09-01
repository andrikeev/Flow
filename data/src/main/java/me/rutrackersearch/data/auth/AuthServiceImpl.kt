package me.rutrackersearch.data.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.rutrackersearch.data.converters.parseAuthResponse
import me.rutrackersearch.data.converters.readJson
import me.rutrackersearch.data.converters.toFailure
import me.rutrackersearch.data.network.BASE_URL
import me.rutrackersearch.domain.entity.auth.AuthResponse
import me.rutrackersearch.domain.repository.AuthService
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthServiceImpl @Inject constructor() : AuthService {
    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor(
            HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.HEADERS)
        )
        .build()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?
    ): AuthResponse {
        return withContext(Dispatchers.IO) {
            val request = buildRequest(username, password, captchaSid, captchaCode, captchaValue)
            kotlin.runCatching {
                requireNotNull(okHttpClient.newCall(request).execute().body)
                    .readJson()
                    .parseAuthResponse()
            }
                .fold(
                    { it },
                    { AuthResponse.Error(it.toFailure()) },
                )
        }
    }

    private fun buildRequest(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?
    ): Request {
        val formData = FormBody.Builder().apply {
            addEncoded("username", username)
            addEncoded("password", password)
            if (captchaSid != null) {
                addEncoded("cap_sid", captchaSid)
            }
            if (captchaCode != null) {
                addEncoded("cap_code", captchaCode)
            }
            if (captchaValue != null) {
                addEncoded("cap_val", captchaValue)
            }
        }.build()
        return Request.Builder()
            .url("$BASE_URL/login")
            .post(formData)
            .build()
    }
}
