package flow.topic.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.PagingAction
import flow.domain.model.append
import flow.domain.model.prepend
import flow.domain.model.refresh
import flow.domain.model.retry
import flow.domain.usecase.AddCommentUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveFavoriteStateUseCase
import flow.domain.usecase.ObserveTopicPagingDataUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import flow.topic.open.id
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    loggerFactory: LoggerFactory,
    private val addCommentUseCase: AddCommentUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase,
    private val observeTopicPagingDataUseCase: ObserveTopicPagingDataUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(), ContainerHost<TopicScreenState, TopicSideEffect> {
    private val logger = loggerFactory.get("TopicViewModel")
    private val id = savedStateHandle.id
    private val pagingActions = MutableSharedFlow<PagingAction>()
    private val lastVisibleIndex = MutableStateFlow(0)

    override val container: Container<TopicScreenState, TopicSideEffect> = container(
        initialState = TopicScreenState(),
        onCreate = {
            observeTopicModel()
            observePagingData()
        },
    )

    fun perform(action: TopicAction) {
        when (action) {
            is TopicAction.AddComment -> onAddComment(action.comment)
            is TopicAction.AddCommentClick -> onAddCommentClick()
            is TopicAction.BackClick -> onBackClick()
            is TopicAction.FavoriteClick -> onFavoriteClick()
            is TopicAction.LastVisibleIndexChanged -> onLastVisibleIndexChanged(action.index)
            is TopicAction.GoToPage -> onGoToPage(action.page)
            is TopicAction.ListBottomReached -> onListBottomReached()
            is TopicAction.ListTopReached -> onListTopReached()
            is TopicAction.LoginClick -> onLoginClick()
            is TopicAction.RetryClick -> onRetryClick()
        }
    }

    private fun observeTopicModel() = viewModelScope.launch {
        observeFavoriteStateUseCase(id).collectLatest { isFavorite ->
            val favoriteState = TopicFavoriteState.FavoriteState(isFavorite)
            intent { reduce { state.copy(favoriteState = favoriteState) } }
        }
    }

    private fun observePagingData() = viewModelScope.launch {
        observeTopicPagingDataUseCase(
            id = id,
            actions = pagingActions,
            scope = viewModelScope,
        )
            .onEach { (_, _, pagination) ->
                viewModelScope.launch {
                    lastVisibleIndex.collectLatest { lastVisibleIndex ->
                        intent {
                            reduce {
                                state.copy(
                                    paginationState = PaginationState.Pagination(
                                        page = pagination.loadedPages.first + (lastVisibleIndex + 1) / PageSize,
                                        pages = pagination.totalPages,
                                    )
                                )
                            }
                        }
                    }
                }
            }
            .collectLatest { (data, loadStates) ->
                intent {
                    reduce {
                        state.copy(
                            topicContent = when {
                                data == null -> TopicContent.Initial
                                data.isEmpty() -> TopicContent.Empty
                                else -> TopicContent.Posts(data)
                            },
                            loadStates = loadStates,
                        )
                    }
                }
            }
    }

    private fun onAddComment(comment: String) = viewModelScope.launch {
        if (addCommentUseCase(id, comment)) {
            pagingActions.refresh()
        } else {
            intent { postSideEffect(TopicSideEffect.ShowAddCommentError) }
        }
    }

    private fun onAddCommentClick() = intent {
        if (observeAuthStateUseCase().firstOrNull().isAuthorized) {
            postSideEffect(TopicSideEffect.ShowAddCommentDialog)
        } else {
            postSideEffect(TopicSideEffect.ShowLoginRequired)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(TopicSideEffect.Back)
    }

    private fun onFavoriteClick() = viewModelScope.launch {
        toggleFavoriteUseCase(id)
    }

    private fun onLastVisibleIndexChanged(index: Int) = viewModelScope.launch {
        lastVisibleIndex.emit(index)
    }

    private fun onGoToPage(page: Int) = viewModelScope.launch {
        pagingActions.refresh(page)
    }

    private fun onListBottomReached() = viewModelScope.launch {
        pagingActions.append()
    }

    private fun onListTopReached() = viewModelScope.launch {
        pagingActions.prepend()
    }

    private fun onRetryClick() = viewModelScope.launch {
        pagingActions.retry()
    }

    private fun onLoginClick() = intent {
        postSideEffect(TopicSideEffect.OpenLogin)
    }

    private companion object {
        const val PageSize = 30
    }
}
