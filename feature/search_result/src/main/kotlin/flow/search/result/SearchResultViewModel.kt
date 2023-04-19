package flow.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.PagingAction
import flow.domain.model.append
import flow.domain.model.retry
import flow.domain.usecase.AddSearchHistoryUseCase
import flow.domain.usecase.EnrichFilterUseCase
import flow.domain.usecase.ObserveSearchPagingDataUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class SearchResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    loggerFactory: LoggerFactory,
    private val observeSearchPagingDataUseCase: ObserveSearchPagingDataUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val enrichFilterUseCase: EnrichFilterUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(), ContainerHost<SearchPageState, SearchResultSideEffect> {
    private val logger = loggerFactory.get("SearchResultViewModel")
    private val mutableFilter = MutableStateFlow(savedStateHandle.filter)
    private val pagingActions = MutableSharedFlow<PagingAction>()

    override val container: Container<SearchPageState, SearchResultSideEffect> = container(
        initialState = SearchPageState(mutableFilter.value),
        onCreate = {
            observeFilter()
            observePagingData()
        },
    )

    fun perform(action: SearchResultAction) {
        logger.d { "Perform $action" }
        when (action) {
            is SearchResultAction.BackClick -> onBackClick()
            is SearchResultAction.ExpandAppBarClick -> onExpandAppBarClick()
            is SearchResultAction.FavoriteClick -> onFavoriteClick(action.torrent)
            is SearchResultAction.ListBottomReached -> onListBottomReached()
            is SearchResultAction.RetryClick -> onRetryClick()
            is SearchResultAction.SearchClick -> onSearchClick()
            is SearchResultAction.SetAuthor -> onSetAuthor(action.author)
            is SearchResultAction.SetCategories -> onSetCategories(action.categories)
            is SearchResultAction.SetOrder -> onSetOrder(action.order)
            is SearchResultAction.SetPeriod -> onSetPeriod(action.period)
            is SearchResultAction.SetSort -> onSetSort(action.sort)
            is SearchResultAction.TorrentClick -> onTorrentClick(action.torrent)
        }
    }

    private fun observeFilter() = viewModelScope.launch {
        mutableFilter
            .onStart {
                intent {
                    val filter = enrichFilterUseCase(state.filter)
                    addSearchHistoryUseCase(filter)
                    reduce { state.copy(filter = filter) }
                }
            }
            .collectLatest { filter ->
                intent { reduce { state.copy(filter = filter) } }
            }
    }

    private fun observePagingData() = viewModelScope.launch {
        logger.d { "Start observing paging data" }
        observeSearchPagingDataUseCase(
            filterFlow = mutableFilter,
            actionsFlow = pagingActions,
            scope = viewModelScope,
        )
            .collectLatest { (data, loadingState) ->
                intent {
                    reduce {
                        state.copy(
                            searchContent = when {
                                data == null -> SearchResultContent.Initial
                                data.isEmpty() -> SearchResultContent.Empty
                                else -> SearchResultContent.Content(
                                    torrents = data,
                                    categories = data.mapNotNull { it.topic.category }.distinct(),
                                )

                            },
                            loadStates = loadingState,
                        )
                    }
                }
            }
    }

    private fun onBackClick() = intent {
        postSideEffect(SearchResultSideEffect.Back)
    }

    private fun onExpandAppBarClick() = intent {
        reduce { state.copy(appBarExpanded = !state.appBarExpanded) }
    }

    private fun onFavoriteClick(torrent: TopicModel<Torrent>) = viewModelScope.launch {
        toggleFavoriteUseCase(torrent.topic.id)
    }

    private fun onListBottomReached() = viewModelScope.launch {
        pagingActions.append()
    }

    private fun onRetryClick() = viewModelScope.launch {
        pagingActions.retry()
    }

    private fun onSearchClick() = intent {
        postSideEffect(SearchResultSideEffect.OpenSearchInput(state.filter))
    }

    private fun onSetAuthor(author: Author?) = onFilterChanged { filter ->
        filter.copy(author = author)
    }

    private fun onSetCategories(categories: List<Category>?) = onFilterChanged { filter ->
        filter.copy(categories = categories)
    }

    private fun onSetSort(sort: Sort) = onSortChanged { filter ->
        filter.copy(sort = sort)
    }

    private fun onSetOrder(order: Order) = onSortChanged { filter ->
        filter.copy(order = order)
    }

    private fun onSetPeriod(period: Period) = intent {
        val filter = state.filter.copy(query = null, period = period)
        postSideEffect(SearchResultSideEffect.OpenSearchResult(filter))
    }

    private fun onTorrentClick(torrent: TopicModel<Torrent>) = intent {
        postSideEffect(SearchResultSideEffect.OpenTorrent(torrent.topic))
    }

    private inline fun onSortChanged(crossinline transformer: (Filter) -> Filter) {
        viewModelScope.launch { mutableFilter.emit(transformer(mutableFilter.value)) }
    }

    private inline fun onFilterChanged(crossinline transformer: (Filter) -> Filter) {
        viewModelScope.launch { mutableFilter.emit(transformer(mutableFilter.value)) }
        intent { reduce { state.copy(appBarExpanded = false) } }
    }
}
