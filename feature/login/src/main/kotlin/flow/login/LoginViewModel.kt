package flow.login

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.LoginUseCase
import flow.domain.usecase.ValidateInputUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.AuthResult
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
            is LoginAction.ReloadCaptchaClick -> onReloadCaptchaClick()
            is LoginAction.SubmitClick -> onSubmitClick()
        }
    }

    private fun validateUsername(value: TextFieldValue) = intent {
        reduce {
            state.copy(
                usernameInput = if (validateInputUseCase(value.text)) {
                    InputState.Valid(value)
                } else {
                    InputState.Empty
                },
            )
        }
    }

    private fun validatePassword(value: TextFieldValue) = intent {
        reduce {
            state.copy(
                passwordInput = if (validateInputUseCase(value.text)) {
                    InputState.Valid(value)
                } else {
                    InputState.Empty
                },
            )
        }
    }

    private fun validateCaptcha(value: TextFieldValue) = intent {
        reduce {
            state.copy(
                captchaInput = if (validateInputUseCase(value.text)) {
                    InputState.Valid(value)
                } else {
                    InputState.Empty
                },
            )
        }
    }

    private fun onReloadCaptchaClick() = intent {
        val response = loginUseCase(
            username = state.usernameInput.value.text,
            password = state.passwordInput.value.text,
            captchaSid = null,
            captchaCode = null,
            captchaValue = null,
        )
        when (response) {
            is AuthResult.WrongCredits -> reduce {
                state.copy(
                    isLoading = false,
                    captcha = response.captcha,
                    captchaInput = InputState.Empty,
                )
            }
            is AuthResult.CaptchaRequired -> reduce {
                state.copy(
                    isLoading = false,
                    captcha = response.captcha,
                    captchaInput = InputState.Empty,
                )
            }
            is AuthResult.Error -> Unit
            is AuthResult.Success -> Unit
        }
    }

    private fun onSubmitClick() = intent {
        postSideEffect(LoginSideEffect.HideKeyboard)
        reduce { state.copy(isLoading = true) }
        val response = loginUseCase(
            state.usernameInput.value.text,
            state.passwordInput.value.text,
            state.captcha?.id,
            state.captcha?.code,
            state.captchaInput.value.text,
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
