package flow.account

import flow.domain.usecase.LogoutUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.models.user.AuthState
import flow.testing.repository.TestAuthRepository
import flow.testing.repository.TestAuthRepository.Companion.TestAccount
import flow.testing.repository.TestBookmarksRepository
import flow.testing.repository.TestFavoritesRepository
import flow.testing.repository.TestSearchHistoryRepository
import flow.testing.repository.TestSuggestsRepository
import flow.testing.repository.TestTopicHistoryRepository
import flow.testing.rule.MainDispatcherRule
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

    private val authRepository = TestAuthRepository()

    private val logoutUseCase = LogoutUseCase(
        authRepository = authRepository,
        backgroundService = TestBackgroundService(),
        bookmarksRepository = TestBookmarksRepository(),
        favoritesRepository = TestFavoritesRepository(),
        searchHistoryRepository = TestSearchHistoryRepository(),
        suggestsRepository = TestSuggestsRepository(),
        topicHistoryRepository = TestTopicHistoryRepository(),
    )
    private val observeAuthStateUseCase = ObserveAuthStateUseCase(
        authRepository = authRepository,
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
        authRepository.saveAccount(TestAccount)
        //do
        containerTest.runOnCreate()
        //check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { AuthState.Unauthorized },
                { AuthState.Authorized(TestAccount.name, TestAccount.avatarUrl) },
            )
        }
    }

    @Test
    fun `Unauthorised when ConfirmLogoutClick`() = runTest {
        //set
        val containerTest = viewModel.liveTest()
        authRepository.saveAccount(TestAccount)
        containerTest.runOnCreate()
        //do
        containerTest.testIntent { perform(AccountAction.ConfirmLogoutClick) }
        //check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { AuthState.Authorized(TestAccount.name, TestAccount.avatarUrl) },
                { AuthState.Unauthorized },
            )
        }
    }

    @Test
    fun `Authorized when account saved`() = runTest {
        //set
        val containerTest = viewModel.liveTest()
        authRepository.clear()
        containerTest.runOnCreate()
        //do
        authRepository.saveAccount(TestAccount)
        //check
        containerTest.assert(AuthState.Unauthorized) {
            states(
                { AuthState.Unauthorized },
                { AuthState.Authorized(TestAccount.name, TestAccount.avatarUrl) },
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
    fun `HideLogoutConfirmation when CancelLogoutClick`() = runTest {
        //set
        val containerTest = viewModel.test()
        //do
        containerTest.testIntent { perform(AccountAction.CancelLogoutClick) }
        //check
        containerTest.assert(AuthState.Unauthorized) {
            postedSideEffects(AccountSideEffect.HideLogoutConfirmation)
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
