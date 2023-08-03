package flow.auth.impl

import flow.auth.api.AuthService
import flow.auth.api.TokenProvider
import flow.common.SingleItemMutableSharedFlow
import flow.models.auth.AuthResult
import flow.models.auth.AuthState
import flow.models.auth.Captcha
import flow.network.api.NetworkApi
import flow.network.dto.auth.AuthResponseDto
import flow.network.dto.auth.CaptchaDto
import flow.securestorage.PreferencesStorage
import flow.securestorage.model.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AuthServiceImpl @Inject constructor(
    private val api: NetworkApi,
    private val preferencesStorage: PreferencesStorage,
) : AuthService, TokenProvider {
    private val mutableAuthState = SingleItemMutableSharedFlow<AuthState>()

    override fun observeAuthState(): Flow<AuthState> = mutableAuthState
        .asSharedFlow()
        .onStart { emit(getAuthState()) }

    override suspend fun isAuthorized(): Boolean = getAuthState() is AuthState.Authorized

    override suspend fun getToken(): String = preferencesStorage.getAccount()?.token.orEmpty()

    override suspend fun login(
        username: String,
        password: String,
        captchaSid: String?,
        captchaCode: String?,
        captchaValue: String?,
    ): AuthResult {
        fun CaptchaDto?.toCaptcha(): Captcha? = this?.let { Captcha(id, code, url) }
        return when (val dto =
            api.login(username, password, captchaSid, captchaCode, captchaValue)) {
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
        val account = preferencesStorage.getAccount()
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
        preferencesStorage.clearAccount()
        mutableAuthState.emit(AuthState.Unauthorized)
    }

    private suspend fun saveAccount(account: Account) {
        preferencesStorage.saveAccount(account)
        mutableAuthState.emit(AuthState.Authorized(account.name, account.avatarUrl))
    }

    private suspend fun getAuthState(): AuthState {
        val account = preferencesStorage.getAccount()
        return if (account != null) {
            AuthState.Authorized(account.name, account.avatarUrl)
        } else {
            AuthState.Unauthorized
        }
    }
}
