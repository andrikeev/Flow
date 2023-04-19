package flow.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.LoginUseCase
import flow.domain.usecase.ValidateInputUseCase
import flow.logger.api.LoggerFactory
import flow.models.InputState
import flow.models.auth.AuthResult
import kotlinx.coroutines.launch
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
    private val validateInputUseCase: ValidateInputUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {
    private val logger = loggerFactory.get("LoginViewModel")

    override val container: Container<LoginState, LoginSideEffect> = container(LoginState())

    fun perform(action: LoginAction) {
        logger.d { "Perform $action" }
        when (action) {
            is LoginAction.UsernameChanged -> validateUsername(action.value)
            is LoginAction.PasswordChanged -> validatePassword(action.value)
            is LoginAction.CaptchaChanged -> validateCaptcha(action.value)
            is LoginAction.SubmitClick -> submit()
        }
    }

    private fun validateUsername(value: String) = intent {
        reduce { state.copy(usernameInput = validateInputUseCase(value)) }
    }

    private fun validatePassword(value: String) = intent {
        reduce { state.copy(passwordInput = validateInputUseCase(value)) }
    }

    private fun validateCaptcha(value: String) = intent {
        reduce { state.copy(captchaInput = validateInputUseCase(value)) }
    }

    private fun submit() = viewModelScope.launch {
        intent {
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
                is AuthResult.Success -> {
                    logger.d { "Login success" }
                    postSideEffect(LoginSideEffect.Success)
                }

                is AuthResult.CaptchaRequired -> {
                    logger.d { "Login failed: captcha required" }
                    reduce {
                        state.copy(
                            isLoading = false,
                            captcha = response.captcha,
                            captchaInput = InputState.Empty,
                        )
                    }
                }

                is AuthResult.WrongCredits -> {
                    logger.d { "Login failed: wrong credits" }
                    reduce {
                        state.copy(
                            isLoading = false,
                            usernameInput = InputState.Invalid(state.usernameInput.value),
                            passwordInput = InputState.Invalid(state.passwordInput.value),
                            captcha = response.captcha,
                            captchaInput = InputState.Empty,
                        )
                    }
                }

                is AuthResult.Error -> {
                    logger.e(response.error) { "Login error" }
                    postSideEffect(LoginSideEffect.Error(response.error))
                    reduce { state.copy(isLoading = false) }
                }
            }
        }
    }
}
