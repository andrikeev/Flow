package me.rutrackersearch.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.rutrackersearch.auth.models.AuthResponse
import me.rutrackersearch.domain.usecase.LoginUseCase
import me.rutrackersearch.domain.usecase.SaveAccountUseCase
import me.rutrackersearch.domain.usecase.TextValidationUseCase
import me.rutrackersearch.models.InputState
import me.rutrackersearch.models.user.Account
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val textValidationUseCase: TextValidationUseCase,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {
    override val container: Container<LoginState, LoginSideEffect> = container(LoginState())

    fun perform(action: LoginAction) = when (action) {
        is LoginAction.UsernameChanged -> validateUsername(action.value)
        is LoginAction.PasswordChanged -> validatePassword(action.value)
        is LoginAction.CaptchaChanged -> validateCaptcha(action.value)
        is LoginAction.SubmitClick -> submit()
    }

    private fun validateUsername(value: String) = intent {
        reduce { state.copy(usernameInput = textValidationUseCase(value)) }
    }

    private fun validatePassword(value: String) = intent {
        reduce { state.copy(passwordInput = textValidationUseCase(value)) }
    }

    private fun validateCaptcha(value: String) = intent {
        reduce { state.copy(captchaInput = textValidationUseCase(value)) }
    }

    private fun submit() = intent {
        postSideEffect(LoginSideEffect.HideKeyboard)
        if (state.isValid) {
            reduce { state.copy(isLoading = true) }
            viewModelScope.launch {
                val response = loginUseCase(
                    state.usernameInput.value,
                    state.passwordInput.value,
                    state.captcha?.id,
                    state.captcha?.code,
                    state.captchaInput.value,
                )
                when (response) {
                    is AuthResponse.Success -> {
                        saveAccountUseCase(
                            Account(
                                response.accountData.id,
                                state.usernameInput.value,
                                state.passwordInput.value,
                                response.accountData.token,
                                response.accountData.avatarUrl,
                            )
                        )
                        postSideEffect(LoginSideEffect.Success)
                    }

                    is AuthResponse.CaptchaRequired -> reduce {
                        state.copy(
                            isLoading = false,
                            captcha = response.captcha,
                            captchaInput = InputState.Empty,
                        )
                    }

                    is AuthResponse.WrongCredits -> reduce {
                        state.copy(
                            isLoading = false,
                            usernameInput = InputState.Invalid(state.usernameInput.value),
                            passwordInput = InputState.Invalid(state.passwordInput.value),
                            captcha = response.captcha,
                            captchaInput = InputState.Empty,
                        )
                    }

                    is AuthResponse.Error -> {
                        postSideEffect(LoginSideEffect.Error(response.error))
                        reduce { state.copy(isLoading = false) }
                    }
                }
            }
        }
    }
}
