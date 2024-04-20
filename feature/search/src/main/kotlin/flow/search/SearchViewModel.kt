package flow.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.search.isEmpty
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveSearchHistoryUseCase
import flow.domain.usecase.PinSearchHistoryUseCase
import flow.domain.usecase.RemoveSearchHistoryUseCase
import flow.domain.usecase.UnpinSearchHistoryUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import flow.models.search.Search
import kotlinx.coroutines.flow.collectLatest
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
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
    private val removeSearchHistoryUseCase: RemoveSearchHistoryUseCase,
    private val pinSearchHistoryUseCase: PinSearchHistoryUseCase,
    private val unpinSearchHistoryUseCase: UnpinSearchHistoryUseCase,
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
            is SearchAction.DeleteItemClick -> onDeleteItemClick(action.search)
            is SearchAction.LoginClick -> onLoginClick()
            is SearchAction.PinItemClick -> onPinItemClick(action.search)
            is SearchAction.SearchActionClick -> onSearchActionClick()
            is SearchAction.SearchItemClick -> onSearchItemClick(action.search)
            is SearchAction.UnpinItemClick -> onUnpinItemClick(action.search)
        }
    }

    private fun observeSearchHistory() = intent {
        logger.d { "Start observing search history" }
        observeAuthStateUseCase().collectLatest { authState ->
            if (authState.isAuthorized) {
                observeSearchHistoryUseCase().collectLatest { searchHistory ->
                    reduce {
                        if (searchHistory.isEmpty()) {
                            SearchState.Empty
                        } else {
                            SearchState.SearchList(
                                pinned = searchHistory.pinned,
                                other = searchHistory.other,
                            )
                        }
                    }
                }
            } else {
                reduce { SearchState.Unauthorised }
            }
        }
    }

    private fun onDeleteItemClick(search: Search) = viewModelScope.launch {
        removeSearchHistoryUseCase.invoke(search)
    }

    private fun onLoginClick() = intent {
        postSideEffect(SearchSideEffect.OpenLogin)
    }

    private fun onPinItemClick(search: Search) = viewModelScope.launch {
        pinSearchHistoryUseCase.invoke(search)
    }

    private fun onSearchActionClick() = intent {
        postSideEffect(SearchSideEffect.OpenSearchInput)
    }

    private fun onSearchItemClick(search: Search) = intent {
        postSideEffect(SearchSideEffect.OpenSearch(search.filter))
    }

    private fun onUnpinItemClick(search: Search) = viewModelScope.launch {
        unpinSearchHistoryUseCase.invoke(search)
    }
}
