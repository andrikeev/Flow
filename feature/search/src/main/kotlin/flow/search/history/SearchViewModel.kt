package flow.search.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.models.user.isAuthorized
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
) : ViewModel(), ContainerHost<SearchState, SearchSideEffect> {
    override val container: Container<SearchState, SearchSideEffect> = container(
        initialState = SearchState.Initial,
        onCreate = { observeSearchHistory() },
    )

    fun perform(action: SearchAction) = intent {
        when (action) {
            is SearchAction.LoginClick -> postSideEffect(SearchSideEffect.OpenLogin)
            is SearchAction.SearchActionClick -> postSideEffect(SearchSideEffect.OpenSearchInput)
            is SearchAction.SearchItemClick -> postSideEffect(SearchSideEffect.OpenSearch(action.search.filter))
        }
    }

    private fun observeSearchHistory() {
        intent {
            viewModelScope.launch {
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
                    }.collectLatest { state -> reduce { state } }
            }
        }
    }
}
