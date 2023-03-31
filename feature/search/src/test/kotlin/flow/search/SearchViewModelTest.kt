package flow.search

import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.models.search.Filter
import flow.models.search.Search
import flow.testing.repository.TestSearchHistoryRepository
import flow.testing.rule.MainDispatcherRule
import flow.testing.service.TestAuthService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test

class SearchViewModelTest {
    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val authService = TestAuthService()
    private val searchHistoryRepository = TestSearchHistoryRepository()
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        viewModel = SearchViewModel(
            observeAuthStateUseCase = ObserveAuthStateUseCase(authService),
            observeSearchHistoryUseCase = ObserveSearchHistoryUseCase(searchHistoryRepository)
        )
    }

    @Test
    fun `Initial state`() = runTest {
        // set
        val containerTest = viewModel.test()
        // check
        containerTest.assert(SearchState.Initial)
    }

    @Test
    fun `Unauthorised state then empty then list with search`() = runTest {
        // set
        val containerTest = viewModel.test()
        containerTest.runOnCreate()
        // do
        authService.authState.value = TestAuthService.TestAuthState
        searchHistoryRepository.addSearch(Filter())
        // check
        containerTest.assert(SearchState.Initial) {
            states(
                { SearchState.Unauthorised },
                { SearchState.Empty },
                { SearchState.SearchList(listOf(Search(0, Filter()))) },
            )
        }
    }
}
