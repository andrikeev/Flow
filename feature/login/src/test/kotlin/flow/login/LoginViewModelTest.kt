package flow.login

import flow.domain.usecase.LoginUseCase
import flow.domain.usecase.TextValidationUseCase
import flow.models.InputState
import flow.models.auth.AuthResult
import flow.models.auth.Captcha
import flow.testing.logger.TestLoggerFactory
import flow.testing.rule.MainDispatcherRule
import flow.testing.service.TestAuthService
import flow.testing.service.TestBackgroundService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test

class LoginViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val authService = TestAuthService()
    private val backgroundService = TestBackgroundService()

    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(
            loginUseCase = LoginUseCase(authService, backgroundService),
            textValidationUseCase = TextValidationUseCase(),
            loggerFactory = TestLoggerFactory(),
        )
    }

    @Test
    fun `Initial state`() = runTest {
        // set
        val containerTest = viewModel.test()
        // check
        containerTest.assert(LoginState())
    }

    @Test
    fun `Valid inputs`() = runTest {
        // set
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.UsernameChanged("Username")) }
        containerTest.testIntent { perform(LoginAction.PasswordChanged("Password")) }
        containerTest.testIntent { perform(LoginAction.CaptchaChanged("Captcha")) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(usernameInput = InputState.Valid("Username")) },
                { copy(passwordInput = InputState.Valid("Password")) },
                { copy(captchaInput = InputState.Valid("Captcha")) },
            )
        }
    }

    @Test
    fun `Invalid empty inputs`() = runTest {
        // set
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.UsernameChanged("")) }
        containerTest.testIntent { perform(LoginAction.PasswordChanged("")) }
        containerTest.testIntent { perform(LoginAction.CaptchaChanged("")) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(usernameInput = InputState.Empty) },
                { copy(passwordInput = InputState.Empty) },
                { copy(captchaInput = InputState.Empty) },
            )
        }
    }

    @Test
    fun `Submit and receive Success`() = runTest {
        // set
        authService.response = AuthResult.Success
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.SubmitClick) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(isLoading = true) },
            )
            postedSideEffects(
                LoginSideEffect.HideKeyboard,
                LoginSideEffect.Success,
            )
        }
    }

    @Test
    fun `Submit and receive WrongCredits without Captcha`() = runTest {
        // set
        authService.response = AuthResult.WrongCredits(null)
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.SubmitClick) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(isLoading = true) },
                {
                    copy(
                        isLoading = false,
                        usernameInput = InputState.Invalid(usernameInput.value),
                        passwordInput = InputState.Invalid(passwordInput.value),
                        captchaInput = InputState.Empty,
                    )
                },
            )
            postedSideEffects(
                LoginSideEffect.HideKeyboard,
            )
        }
    }

    @Test
    fun `Submit and receive WrongCredits with Captcha`() = runTest {
        // set
        authService.response = AuthResult.WrongCredits(TestCaptcha)
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.SubmitClick) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(isLoading = true) },
                {
                    copy(
                        isLoading = false,
                        usernameInput = InputState.Invalid(usernameInput.value),
                        passwordInput = InputState.Invalid(passwordInput.value),
                        captcha = TestCaptcha,
                        captchaInput = InputState.Empty,
                    )
                },
            )
            postedSideEffects(
                LoginSideEffect.HideKeyboard,
            )
        }
    }

    @Test
    fun `Submit and receive Captcha`() = runTest {
        // set
        authService.response = AuthResult.CaptchaRequired(TestCaptcha)
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.SubmitClick) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(isLoading = true) },
                {
                    copy(
                        isLoading = false,
                        captcha = TestCaptcha,
                        captchaInput = InputState.Empty,
                    )
                },
            )
            postedSideEffects(
                LoginSideEffect.HideKeyboard,
            )
        }
    }

    @Test
    fun `Submit and receive Error`() = runTest {
        // set
        authService.response = AuthResult.Error(TestError)
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(LoginAction.SubmitClick) }
        // check
        containerTest.assert(LoginState()) {
            states(
                { copy(isLoading = true) },
                { copy(isLoading = false) },
            )
            postedSideEffects(
                LoginSideEffect.HideKeyboard,
                LoginSideEffect.Error(TestError),
            )
        }
    }

    companion object {
        val TestCaptcha = Captcha("123", "abcd", "example.com")
        val TestError = RuntimeException()
    }
}
