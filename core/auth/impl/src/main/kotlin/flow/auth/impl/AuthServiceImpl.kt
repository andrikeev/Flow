package flow.auth.impl

import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.models.auth.AuthResult
import flow.models.auth.AuthState
import flow.models.auth.Captcha
import flow.network.api.NetworkApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.auth.CaptchaDto
import flow.securestorage.SecureStorage
import flow.securestorage.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthServiceImpl @Inject constructor(
    private val api: NetworkApi,
    private val secureStorage: SecureStorage,
) : AuthService, TokenProvider {
    private val mutableAuthState: MutableStateFlow<AuthState> by lazy { MutableStateFlow(initAuthState()) }

    override fun observeAuthState(): Flow<AuthState> = mutableAuthState.asStateFlow()

    override fun isAuthorized(): Boolean = mutableAuthState.value is AuthState.Authorized

    override fun getToken(): String = secureStorage.getAccount()?.token.orEmpty()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResult {
        return when (val dto = api.login(username, password, captchaSid, captchaCode, captchaValue)) {
            is AuthResponseDto.CaptchaRequired -> {
                AuthResult.CaptchaRequired(requireNotNull(dto.captcha.toCaptcha()))
            }
            is AuthResponseDto.Success -> {
                val (id, token, avatarUrl) = dto.user
                saveAccount(Account(id, username, password, token, avatarUrl))
                AuthResult.Success
            }
            is AuthResponseDto.WrongCredits -> {
                AuthResult.WrongCredits(dto.captcha.toCaptcha())
            }
        }
    }

    override suspend fun refreshToken(): Boolean {
        val account = secureStorage.getAccount()
        if (account != null) {
            val dto = api.login(account.name, account.password, null, null, null)
            if (dto is AuthResponseDto.Success) {
                saveAccount(account.copy(token = dto.user.token))
                return true
            }
        }
        logout()
        return false
    }

    override suspend fun logout() {
        secureStorage.clearAccount()
        mutableAuthState.emit(AuthState.Unauthorized)
    }

    private fun initAuthState(): AuthState {
        val account = secureStorage.getAccount()
        return if (account != null) {
            AuthState.Authorized(account.name, account.avatarUrl)
        } else {
            AuthState.Unauthorized
        }
    }

    private suspend fun saveAccount(account: Account) {
        secureStorage.saveAccount(account)
        mutableAuthState.emit(AuthState.Authorized(account.name, account.avatarUrl))
    }

    private fun CaptchaDto?.toCaptcha(): Captcha? = this?.let { Captcha(id, code, url) }
}
