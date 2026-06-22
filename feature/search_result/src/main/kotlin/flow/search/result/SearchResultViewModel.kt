package flow.search.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import flow.common.runSuspendCatching
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
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

internal class SearchResultViewModel(
    initialFilter: Filter,
    loggerFactory: LoggerFactory,
    private val observeSearchPagingDataUseCase: ObserveSearchPagingDataUseCase,
    private val addSearchHistoryUseCase: AddSearchHistoryUseCase,
    private val enrichFilterUseCase: EnrichFilterUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(), ContainerHost<SearchPageState, SearchResultSideEffect> {
    private val logger = loggerFactory.get("SearchResultViewModel")
    private val mutableFilter = MutableStateFlow(initialFilter)
    private val pagingActions = MutableSharedFlow<PagingAction>()


    override val container: Container<SearchPageState, SearchResultSideEffect> = container(
        initialState = SearchPageState(mutableFilter.value),
        onCreate = {
            val enriched = enrichFilterUseCase(state.filter)
            mutableFilter.emit(enriched)
            addSearchHistoryUseCase(enriched)
            repeatOnSubscription {
                launch {
                    mutableFilter.collectLatest { filter ->
                        reduce { state.copy(filter = filter) }
                    }
                }
                launch {
                    logger.d { "Start observing paging data" }
                    observeSearchPagingDataUseCase(
                        filterFlow = mutableFilter,
                        actionsFlow = pagingActions,
                        scope = viewModelScope,
                    ).collectLatest { (data, loadingState) ->
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
        },
    )

    fun perform(action: SearchResultAction) {
        logger.d { "Perform $action" }
        when (action) {
            is SearchResultAction.BackClick -> onBackClick()
            is SearchResultAction.ExpandAppBarClick -> onExpandAppBarClick()
            is SearchResultAction.FavoriteClick -> onFavoriteClick(action.topicModel)
            is SearchResultAction.ListBottomReached -> onListBottomReached()
            is SearchResultAction.RetryClick -> onRetryClick()
            is SearchResultAction.SearchClick -> onSearchClick()
            is SearchResultAction.SetAuthor -> onSetAuthor(action.author)
            is SearchResultAction.SetCategories -> onSetCategories(action.categories)
            is SearchResultAction.SetOrder -> onSetOrder(action.order)
            is SearchResultAction.SetPeriod -> onSetPeriod(action.period)
            is SearchResultAction.SetSort -> onSetSort(action.sort)
            is SearchResultAction.TopicClick -> onTopicClick(action.topicModel)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(SearchResultSideEffect.Back)
    }

    private fun onExpandAppBarClick() = intent {
        reduce { state.copy(appBarExpanded = !state.appBarExpanded) }
    }

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) = intent {
        runSuspendCatching { toggleFavoriteUseCase(topicModel.topic.id) }
            .onFailure { postSideEffect(SearchResultSideEffect.ShowFavoriteToggleError) }
    }

    private fun onListBottomReached() = intent {
        pagingActions.append()
    }

    private fun onRetryClick() = intent {
        pagingActions.retry()
    }

    private fun onSearchClick() = intent {
        val filter = state.filter.copy(period = Period.ALL_TIME)
        postSideEffect(SearchResultSideEffect.OpenSearchInput(filter))
    }

    private fun onSetAuthor(author: Author?) = intent {
        updateFilter { copy(author = author) }
        reduce { state.copy(appBarExpanded = false) }
    }

    private fun onSetCategories(categories: List<Category>?) = intent {
        updateFilter { copy(categories = categories) }
        reduce { state.copy(appBarExpanded = false) }
    }

    private fun onSetSort(sort: Sort) = intent {
        updateFilter { copy(sort = sort) }
    }

    private fun onSetOrder(order: Order) = intent {
        updateFilter { copy(order = order) }
    }

    private suspend fun updateFilter(transform: Filter.() -> Filter) {
        val updated = mutableFilter.value.transform()
        mutableFilter.emit(updated)
        addSearchHistoryUseCase(updated)
    }

    private fun onSetPeriod(period: Period) = intent {
        val filter = state.filter.copy(query = null, period = period)
        postSideEffect(SearchResultSideEffect.OpenSearchResult(filter))
    }

    private fun onTopicClick(topicModel: TopicModel<out Topic>) = intent {
        postSideEffect(SearchResultSideEffect.OpenTopic(topicModel.topic.id))
    }
}
