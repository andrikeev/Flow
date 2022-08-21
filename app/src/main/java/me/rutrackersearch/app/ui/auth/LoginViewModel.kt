package me.rutrackersearch.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.auth.models.AuthResponse
import me.rutrackersearch.auth.models.Captcha
import me.rutrackersearch.models.user.Account
import me.rutrackersearch.domain.usecase.LoginUseCase
import me.rutrackersearch.domain.usecase.SaveAccountUseCase
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val saveAccountUseCase: SaveAccountUseCase,
    private val loginUseCase: LoginUseCase,
) : ViewModel() {

    private val mutableUsernameInput = MutableStateFlow<InputState>(InputState.Initial)
    private val mutablePasswordInput = MutableStateFlow<InputState>(InputState.Initial)
    private val mutableCaptchaInput = MutableStateFlow<InputState>(InputState.Initial)
    private val mutableLoadingState = MutableStateFlow<LoginStatus>(LoginStatus.Initial)
    private val mutableCaptchaState = MutableStateFlow<Captcha?>(null)

    val state: StateFlow<LoginState> = combine(
        mutableUsernameInput,
        mutablePasswordInput,
        mutableCaptchaState,
        mutableCaptchaInput,
        mutableLoadingState,
    ) { usernameInput, passwordInput, captcha, captchaInput, loginState ->
        LoginState(
            isSuccess = loginState is LoginStatus.Success,
            isLoading = loginState is LoginStatus.Loading,
            usernameInput = usernameInput,
            passwordInput = passwordInput,
            captcha = captcha,
            captchaInput = captchaInput,
            error = loginState.takeIfInstance<LoginStatus.Error>()?.error,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, LoginState())

    fun perform(action: LoginAction) {
        when (action) {
            is LoginAction.UsernameChanged -> {
                mutableUsernameInput.value = if (action.value.isNotBlank()) {
                    InputState.Valid(action.value)
                } else {
                    InputState.Empty
                }
            }
            is LoginAction.PasswordChanged -> {
                mutablePasswordInput.value = if (action.value.isNotBlank()) {
                    InputState.Valid(action.value)
                } else {
                    InputState.Empty
                }
            }
            is LoginAction.CaptchaChanged -> {
                mutableCaptchaInput.value = if (action.value.isNotBlank()) {
                    InputState.Valid(action.value)
                } else {
                    InputState.Empty
                }
            }
            LoginAction.SubmitClick -> viewModelScope.launch { login() }
        }
    }

    private suspend fun login() {
        mutableLoadingState.emit(LoginStatus.Loading)
        val response = loginUseCase(
            mutableUsernameInput.value.value,
            mutablePasswordInput.value.value,
            mutableCaptchaState.value?.id,
            mutableCaptchaState.value?.code,
            mutableCaptchaInput.value.value,
        )
        when (response) {
            is AuthResponse.Success -> {
                saveAccountUseCase(
                    Account(
                        response.accountData.id,
                        mutableUsernameInput.value.value,
                        mutablePasswordInput.value.value,
                        response.accountData.token,
                        response.accountData.avatarUrl,
                    )
                )
                mutableLoadingState.emit(LoginStatus.Success)
            }
            is AuthResponse.CaptchaRequired -> {
                mutableLoadingState.emit(LoginStatus.Error())
                mutableCaptchaState.emit(response.captcha)
                mutableCaptchaInput.emit(InputState.Empty)
            }
            is AuthResponse.WrongCredits -> {
                mutableLoadingState.emit(LoginStatus.Error())
                mutableCaptchaState.emit(response.captcha)
                mutableUsernameInput.emit(InputState.Invalid(mutableUsernameInput.value.value))
                mutablePasswordInput.emit(InputState.Invalid(mutablePasswordInput.value.value))
                mutableCaptchaInput.emit(InputState.Empty)
            }
            is AuthResponse.Error -> {
                mutableLoadingState.emit(LoginStatus.Error(response.error))
            }
        }
    }
}

inline fun <reified T> Any?.takeIfInstance(): T? {
    return if (this is T) {
        this
    } else {
        null
    }
}
