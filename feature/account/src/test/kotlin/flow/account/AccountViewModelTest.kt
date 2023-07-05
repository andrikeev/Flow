package flow.account

import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.auth.AuthState
import flow.testing.logger.TestLoggerFactory
import flow.testing.rule.MainDispatcherRule
import flow.testing.service.TestAuthService
import flow.testing.service.TestAuthService.Companion.TestAuthState
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.liveTest
import org.orbitmvi.orbit.test

internal class AccountViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val authService = TestAuthService()

    private lateinit var viewModel: AccountViewModel

    @Before
    fun setUp() {
        viewModel = AccountViewModel(
            logoutUseCase = object : LogoutUseCase {
                override suspend fun invoke() = authService.logout()
            },
            observeAuthStateUseCase = object : ObserveAuthStateUseCase {
                override fun invoke() = authService.authState
            },
            loggerFactory = TestLoggerFactory(),
        )
    }

    @Test
    fun `Unauthorised when initial`() = runTest {
        // set
        val containerTest = viewModel.test()
        // check
        containerTest.assert(AuthState.Unauthorized)
    }

    @Test
    fun `Authorized when created`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        authService.authState.value = TestAuthState
        // do
        containerTest.runOnCreate()
        // check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { AuthState.Unauthorized },
                { TestAuthState },
            )
        }
    }

    @Test
    fun `Unauthorised when ConfirmLogoutClick`() = runTest {
        // set
        val containerTest = viewModel.liveTest()
        authService.authState.value = TestAuthState
        containerTest.runOnCreate()
        // do
        containerTest.testIntent { perform(AccountAction.ConfirmLogoutClick) }
        // check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { TestAuthState },
                { AuthState.Unauthorized },
            )
        }
    }

    @Test
    fun `ShowLogoutConfirmation when LogoutClick`() = runTest {
        // set
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(AccountAction.LogoutClick) }
        // check
        containerTest.assert(AuthState.Unauthorized) {
            postedSideEffects(AccountSideEffect.ShowLogoutConfirmation)
        }
    }

    @Test
    fun `OpenLogin when LoginClick`() = runTest {
        // set
        val containerTest = viewModel.test()
        // do
        containerTest.testIntent { perform(AccountAction.LoginClick) }
        // check
        containerTest.assert(AuthState.Unauthorized) {
            postedSideEffects(AccountSideEffect.OpenLogin)
        }
    }
}
