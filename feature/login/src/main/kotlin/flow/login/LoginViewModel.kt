package flow.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.auth.models.AuthResponse
import flow.domain.usecase.LoginUseCase
import flow.domain.usecase.SaveAccountUseCase
import flow.domain.usecase.TextValidationUseCase
import flow.logger.api.LoggerFactory
import flow.models.InputState
import flow.models.user.Account
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val textValidationUseCase: TextValidationUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {
    private val logger = loggerFactory.get("LoginViewModel")

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
        reduce { state.copy(isLoading = true) }
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
                        response.account.id,
                        state.usernameInput.value,
                        state.passwordInput.value,
                        response.account.token,
                        response.account.avatarUrl,
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
                logger.e(response.error) { "Login error" }
                postSideEffect(LoginSideEffect.Error(response.error))
                reduce { state.copy(isLoading = false) }
            }
        }
    }
}
