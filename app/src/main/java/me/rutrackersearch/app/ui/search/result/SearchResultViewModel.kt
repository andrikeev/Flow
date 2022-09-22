package me.rutrackersearch.app.ui.search.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireFilter
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.app.ui.paging.LoadStates
import me.rutrackersearch.domain.usecase.AddSearchHistoryUseCase
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.LoadSearchPageUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.search.Order
import me.rutrackersearch.models.search.Period
import me.rutrackersearch.models.search.Sort
import me.rutrackersearch.models.topic.Author
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.utils.newCancelableScope
import me.rutrackersearch.utils.relaunch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SearchResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val loadSearchPageUseCase: LoadSearchPageUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel(), ContainerHost<SearchResultState, SearchResultSideEffect> {
    private val enrichScope = newCancelableScope()

    override val container: Container<SearchResultState, SearchResultSideEffect> = container(
        initialState = SearchResultState(savedStateHandle.requireFilter()),
        onCreate = { loadFirstPage() },
    )

    fun perform(action: SearchResultAction) = when (action) {
        is SearchResultAction.BackClick -> onBackClick()
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

    private fun onFavoriteClick(torrent: TopicModel<Torrent>) {
        viewModelScope.launch { updateFavoriteUseCase(torrent) }
    }

    private fun onListBottomReached() = intent {
        val content = state.content
        if (
            content is SearchResultContent.Content &&
            content.page < content.pages &&
            state.loadStates.append != LoadState.Loading
        ) {
            reduce { state.copy(loadStates = LoadStates.append) }
            runCatching { loadPage(content.page + 1) }.onFailure { error ->
                reduce {
                    state.copy(loadStates = LoadStates(append = LoadState.Error(error)))
                }
            }
        }
    }

    private fun onRetryClick() = loadFirstPage()

    private fun onSearchClick() = intent {
        postSideEffect(SearchResultSideEffect.OpenSearchInput(state.filter))
    }

    private fun onSetAuthor(author: Author?) =
        onFilterChanged { filter -> filter.copy(author = author) }

    private fun onSetCategories(categories: List<Category>?) =
        onFilterChanged { filter -> filter.copy(categories = categories) }

    private fun onSetOrder(order: Order) = onFilterChanged { filter -> filter.copy(order = order) }

    private fun onSetPeriod(period: Period) = intent {
        val filter = state.filter.copy(query = null, period = period)
        postSideEffect(SearchResultSideEffect.OpenSearchResult(filter))
    }

    private fun onSetSort(sort: Sort) = onFilterChanged { filter -> filter.copy(sort = sort) }

    private fun onTorrentClick(torrent: Torrent) = intent {
        postSideEffect(SearchResultSideEffect.OpenTorrent(torrent))

    }

    private inline fun onFilterChanged(crossinline transformer: (Filter) -> Filter) {
        intent { reduce { state.copy(filter = transformer(state.filter)) } }
        loadFirstPage()
    }

    private fun loadFirstPage() = intent {
        viewModelScope.launch { addSearchHistoryUseCase(state.filter) }
        reduce {
            state.copy(
                content = SearchResultContent.Initial,
                loadStates = LoadStates.refresh,
            )
        }
        runCatching(::loadPage).onFailure { error ->
            reduce {
                state.copy(loadStates = LoadStates(refresh = LoadState.Error(error)))
            }
        }
    }

    private fun loadPage(page: Int = 1) = intent {
        viewModelScope.launch {
            val searchPage = loadSearchPageUseCase(state.filter, page)
            enrichScope.relaunch {
                val torrents = LinkedHashSet(state.content.getTorrents())
                    .plus(searchPage.items)
                    .toList()
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

    private fun SearchResultContent.getTorrents(): List<Torrent> = when (this) {
        is SearchResultContent.Content -> torrents.map(TopicModel<Torrent>::topic)
        is SearchResultContent.Empty,
        is SearchResultContent.Initial -> emptyList()
    }
}
