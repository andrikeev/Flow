package flow.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
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
            is SearchAction.LoginClick -> intent { postSideEffect(SearchSideEffect.OpenLogin) }
            is SearchAction.SearchActionClick -> intent { postSideEffect(SearchSideEffect.OpenSearchInput) }
            is SearchAction.SearchItemClick -> intent { postSideEffect(SearchSideEffect.OpenSearch(action.search.filter)) }
        }
    }

    private fun observeSearchHistory() = viewModelScope.launch {
        logger.d { "Start observing search history" }
        observeAuthStateUseCase()
            .flatMapLatest { authState ->
                if (authState.isAuthorized) {
                    observeSearchHistoryUseCase()
                        .catch { emit(emptyList()) }
                        .mapLatest { searches ->
                            if (searches.isEmpty()) {
                                SearchState.Empty
                            } else {
                                SearchState.SearchList(searches)
                            }
                        }
                } else {
                    flowOf(SearchState.Unauthorised)
                }
            }
            .collectLatest { state -> intent { reduce { state } } }
    }
}
