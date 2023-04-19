package flow.account

import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.auth.AuthState
import flow.testing.repository.TestBookmarksRepository
import flow.testing.repository.TestFavoritesRepository
import flow.testing.repository.TestSearchHistoryRepository
import flow.testing.repository.TestSuggestsRepository
import flow.testing.repository.TestVisitedRepository
import flow.testing.rule.MainDispatcherRule
import flow.testing.service.TestAuthService
import flow.testing.service.TestAuthService.Companion.TestAuthState
import flow.testing.service.TestBackgroundService
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

    private val logoutUseCase = LogoutUseCase(
        authService = authService,
        backgroundService = TestBackgroundService(),
        bookmarksRepository = TestBookmarksRepository(),
        favoritesRepository = TestFavoritesRepository(),
        searchHistoryRepository = TestSearchHistoryRepository(),
        suggestsRepository = TestSuggestsRepository(),
        visitedRepository = TestVisitedRepository(),
    )
    private val observeAuthStateUseCase = ObserveAuthStateUseCase(
        authService = authService,
    )

    private lateinit var viewModel: AccountViewModel

    @Before
    fun setUp() {
        viewModel = AccountViewModel(
            logoutUseCase = logoutUseCase,
            observeAuthStateUseCase = observeAuthStateUseCase,
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
        //set
        val containerTest = viewModel.test()
        //do
        containerTest.runOnCreate()
        //check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { AuthState.Unauthorized },
                { TestAuthState },
            )
        }
    }

    @Test
    fun `Unauthorised when ConfirmLogoutClick`() = runTest {
        //set
        val containerTest = viewModel.liveTest()
        authService.authState.value = TestAuthState
        containerTest.runOnCreate()
        //do
        containerTest.testIntent { perform(AccountAction.ConfirmLogoutClick) }
        //check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { TestAuthState },
                { AuthState.Unauthorized },
            )
        }
    }

    @Test
    fun `ShowLogoutConfirmation when LogoutClick`() = runTest {
        //set
        val containerTest = viewModel.test()
        //do
        containerTest.testIntent { perform(AccountAction.LogoutClick) }
        //check
        containerTest.assert(AuthState.Unauthorized) {
            postedSideEffects(AccountSideEffect.ShowLogoutConfirmation)
        }
    }

    @Test
    fun `OpenLogin when LoginClick`() = runTest {
        //set
        val containerTest = viewModel.test()
        //do
        containerTest.testIntent { perform(AccountAction.LoginClick) }
        //check
        containerTest.assert(AuthState.Unauthorized) {
            postedSideEffects(AccountSideEffect.OpenLogin)
        }
    }
}
