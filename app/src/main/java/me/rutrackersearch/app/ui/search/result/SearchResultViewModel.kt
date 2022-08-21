package me.rutrackersearch.app.ui.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireFilter
import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.app.ui.paging.PagingAction
import me.rutrackersearch.app.ui.paging.PagingData
import me.rutrackersearch.app.ui.paging.PagingDataLoader
import me.rutrackersearch.domain.usecase.AddSearchHistoryUseCase
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.LoadSearchPageUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.models.forum.Category
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadSearchPageUseCase: LoadSearchPageUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
) : ViewModel() {

    private val mutableFilter = MutableStateFlow(savedStateHandle.requireFilter())
    private val mutablePagingActions = MutableSharedFlow<PagingAction>(1)
    private val pagingData = mutableFilter
        .onEach(addSearchHistoryUseCase::invoke)
        .flatMapLatest { filter ->
            PagingDataLoader(
                pageSize = 50,
                fetchData = { page -> loadSearchPageUseCase(filter, page) },
                actions = mutablePagingActions,
                scope = viewModelScope,
            ).flow
        }

    val state: StateFlow<SearchResultState> = pagingData
        .flatMapLatest { pagingData ->
            enrichTopicsUseCase(pagingData.items).map { favorableItems ->
                PagingData(
                    favorableItems,
                    pagingData.loadStates
                )
            }
        }
        .map { pagingData ->
            val categories = mutableListOf<Category>()
            categories.addAll(mutableFilter.value.categories.orEmpty())
            SearchResultState(
                filter = mutableFilter.value,
                content = when (val loadState = pagingData.loadStates.refresh) {
                    is LoadState.Loading -> PageResult.Loading()
                    is LoadState.Error -> PageResult.Error(loadState.error)
                    is LoadState.NotLoading -> {
                        val torrents = pagingData.items
                        categories.addAll(torrents.mapNotNull { item -> item.data.category })
                        if (torrents.isNotEmpty()) {
                            PageResult.Content(
                                content = torrents,
                                prepend = pagingData.loadStates.prepend,
                                append = pagingData.loadStates.append,
                            )
                        } else {
                            PageResult.Empty()
                        }
                    }
                },
                categories = categories.distinct(),
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SearchResultState(mutableFilter.value),
        )

    fun perform(action: SearchResultAction) {
        viewModelScope.launch {
            when (action) {
                is SearchResultAction.RetryClick -> mutablePagingActions.emit(PagingAction.Retry)
                is SearchResultAction.FavoriteClick -> updateFavoriteUseCase(action.torrent)
                is SearchResultAction.ListBottomReached -> {
                    mutablePagingActions.emit(PagingAction.Append)
                }
                is SearchResultAction.SetSort -> {
                    mutableFilter.emit(mutableFilter.value.copy(sort = action.value))
                }
                is SearchResultAction.SetOrder -> {
                    mutableFilter.emit(mutableFilter.value.copy(order = action.value))
                }
                is SearchResultAction.SetAuthor -> {
                    mutableFilter.emit(mutableFilter.value.copy(author = action.value))
                }
                is SearchResultAction.SetCategories -> {
                    mutableFilter.emit(mutableFilter.value.copy(categories = action.value))
                }
                else -> Unit
            }
        }
    }
}
