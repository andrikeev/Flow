package flow.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import flow.models.search.Search
import kotlinx.coroutines.flow.collectLatest
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    val observeAuthStateUseCase: ObserveAuthStateUseCase,
    val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<SearchState, SearchSideEffect> {
    private val logger = loggerFactory.get("SearchViewModel")

    override val container: Container<SearchState, SearchSideEffect> = container(
        initialState = SearchState.Initial,
        onCreate = { observeSearchHistory() },
    )

    fun perform(action: SearchAction) {
        logger.d { "Perform $action" }
        when (action) {
            is SearchAction.LoginClick -> onLoginClick()
            is SearchAction.SearchActionClick -> onSearchActionClick()
            is SearchAction.SearchItemClick -> onSearchItemClick(action.search)
        }
    }

    private fun observeSearchHistory() = intent {
        logger.d { "Start observing search history" }
        observeAuthStateUseCase().collectLatest { authState ->
            if (authState.isAuthorized) {
                observeSearchHistoryUseCase().collectLatest { searches ->
                    reduce {
                        if (searches.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.SearchList(searches)
                        }
                    }
                }
            } else {
                reduce { SearchState.Unauthorised }
            }
        }
    }

    private fun onLoginClick() = intent {
        postSideEffect(SearchSideEffect.OpenLogin)
    }

    private fun onSearchActionClick() = intent {
        postSideEffect(SearchSideEffect.OpenSearchInput)
    }

    private fun onSearchItemClick(search: Search) = intent {
        postSideEffect(SearchSideEffect.OpenSearch(search.filter))
    }
}
