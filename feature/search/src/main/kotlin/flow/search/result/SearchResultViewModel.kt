package flow.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.common.launchCatching
import flow.common.newCancelableScope
import flow.common.relaunch
import flow.domain.usecase.AddSearchHistoryUseCase
import flow.domain.usecase.EnrichTopicsUseCase
import flow.domain.usecase.LoadSearchPageUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.search.Order
import flow.models.search.Period
import flow.models.search.Sort
import flow.models.topic.Author
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.ui.args.requireFilter
import flow.ui.component.LoadState
import flow.ui.component.LoadStates
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
internal class SearchResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val loadSearchPageUseCase: LoadSearchPageUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(), ContainerHost<SearchResultState, SearchResultSideEffect> {
    private val enrichScope = viewModelScope.newCancelableScope()

    override val container: Container<SearchResultState, SearchResultSideEffect> = container(
        initialState = SearchResultState(savedStateHandle.requireFilter()),
        onCreate = { state ->
            viewModelScope.launch { addSearchHistoryUseCase(state.filter) }
            loadFirstPage()
        },
    )

    fun perform(action: SearchResultAction) = when (action) {
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


    private fun onBackClick() = intent { postSideEffect(SearchResultSideEffect.Back) }

    private fun onExpandAppBarClick() = intent { reduce { state.copy(isAppBarExpanded = !state.isAppBarExpanded) } }

    private fun onFavoriteClick(torrent: TopicModel<Torrent>) {
        viewModelScope.launch { toggleFavoriteUseCase(torrent) }
    }

    private fun onRetryClick() = loadFirstPage()

    private fun onSearchClick() = intent { postSideEffect(SearchResultSideEffect.OpenSearchInput(state.filter)) }

    private fun onSetAuthor(author: Author?) = onFilterChanged { filter -> filter.copy(author = author) }

    private fun onSetCategories(categories: List<Category>?) =
        onFilterChanged { filter -> filter.copy(categories = categories) }

    private fun onSetSort(sort: Sort) = onSortChanged { filter -> filter.copy(sort = sort) }

    private fun onSetOrder(order: Order) = onSortChanged { filter -> filter.copy(order = order) }

    private fun onSetPeriod(period: Period) = intent {
        val filter = state.filter.copy(query = null, period = period)
        postSideEffect(SearchResultSideEffect.OpenSearchResult(filter))
    }

    private fun onTorrentClick(torrent: Torrent) = intent {
        postSideEffect(SearchResultSideEffect.OpenTorrent(torrent))
    }

    private inline fun onSortChanged(crossinline transformer: (Filter) -> Filter) {
        intent { reduce { state.copy(filter = transformer(state.filter)) } }
        loadFirstPage()
    }

    private inline fun onFilterChanged(crossinline transformer: (Filter) -> Filter) {
        intent {
            reduce {
                state.copy(
                    filter = transformer(state.filter),
                    isAppBarExpanded = false,
                )
            }
        }
        loadFirstPage()
    }

    private fun loadFirstPage() = intent {
        reduce {
            state.copy(
                content = SearchResultContent.Initial,
                loadStates = LoadStates.refresh,
            )
        }
        viewModelScope.launchCatching(
            onFailure = { reduce { state.copy(loadStates = LoadStates(refresh = LoadState.Error(it))) } }
        ) {
            val searchPage = loadSearchPageUseCase(state.filter, 1)
            enrichScope.relaunch {
                val torrents = searchPage.items
                if (torrents.isEmpty()) {
                    reduce {
                        state.copy(
                            content = SearchResultContent.Empty,
                            loadStates = LoadStates(),
                        )
                    }
                } else {
                    val categories = torrents.mapNotNull(Torrent::category).distinct()
                    enrichTopicsUseCase(torrents).collectLatest { torrentModels ->
                        reduce {
                            state.copy(
                                content = SearchResultContent.Content(
                                    torrents = torrentModels,
                                    categories = categories,
                                    page = searchPage.page,
                                    pages = searchPage.pages,
                                ),
                                loadStates = LoadStates(),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onListBottomReached() = intent {
        val content = state.content
        if (
            content is SearchResultContent.Content &&
            content.page < content.pages &&
            state.loadStates.append != LoadState.Loading
        ) {
            reduce { state.copy(loadStates = LoadStates.append) }
            viewModelScope.launchCatching(
                onFailure = { reduce { state.copy(loadStates = LoadStates(refresh = LoadState.Error(it))) } }
            ) {
                val searchPage = loadSearchPageUseCase(state.filter, content.page + 1)
                enrichScope.relaunch {
                    val torrents = LinkedHashSet(content.torrents.map(TopicModel<Torrent>::topic))
                        .plus(searchPage.items)
                        .toList()
                    val categories = torrents.mapNotNull(Torrent::category).distinct()
                    enrichTopicsUseCase(torrents).collectLatest { torrentModels ->
                        reduce {
                            state.copy(
                                content = SearchResultContent.Content(
                                    torrents = torrentModels,
                                    categories = categories,
                                    page = searchPage.page,
                                    pages = searchPage.pages,
                                ),
                                loadStates = LoadStates(),
                            )
                        }
                    }
                }
            }
        }
    }
}
