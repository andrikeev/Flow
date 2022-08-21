package me.rutrackersearch.app.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.common.Result
import me.rutrackersearch.models.search.Search
import me.rutrackersearch.models.user.isAuthorized
import me.rutrackersearch.domain.usecase.ObserveAuthStateUseCase
import me.rutrackersearch.domain.usecase.ObserveSearchHistoryUseCase
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    observeAuthStateUseCase: ObserveAuthStateUseCase,
    val observeSearchHistoryUseCase: ObserveSearchHistoryUseCase,
) : ViewModel() {

    private val mutableSearchHistoryState =
        MutableStateFlow<Result<List<Search>>>(Result.Loading())

    val state: StateFlow<SearchState> = combine(
        observeAuthStateUseCase(),
        mutableSearchHistoryState,
    ) { authState, loadingState ->
        if (authState.isAuthorized()) {
            when (loadingState) {
                is Result.Content -> {
                    if (loadingState.content.isEmpty()) {
                        SearchState.Empty
                    } else {
                        SearchState.SearchList(loadingState.content)
                    }
                }
                is Result.Error -> SearchState.Empty
                is Result.Loading -> SearchState.Initial
            }
        } else {
            SearchState.Unauthorised
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = SearchState.Initial)

    init {
        viewModelScope.launch {
            observeSearchHistoryUseCase()
                .catch { error ->
                    mutableSearchHistoryState.emit(Result.Error(error))
                }
                .collectLatest { items ->
                    mutableSearchHistoryState.emit(Result.Content(items))
                }
        }
    }
}
