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
import flow.domain.usecase.EnrichTopicUseCase
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveTopicPagingDataUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.domain.usecase.VisitTopicUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.isAuthorized
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
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
    private val authStateUseCase: ObserveAuthStateUseCase,
    private val enrichTopicUseCase: EnrichTopicUseCase,
    private val observeTopicPagingDataUseCase: ObserveTopicPagingDataUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val visitTopicUseCase: VisitTopicUseCase,
) : ViewModel(), ContainerHost<TopicPageState, TopicSideEffect> {
    private val logger = loggerFactory.get("TopicViewModel")
    private val topic = savedStateHandle.topic
    private val pagingActions = MutableSharedFlow<PagingAction>()
    private val lastVisibleIndex = MutableStateFlow(0)

    override val container: Container<TopicPageState, TopicSideEffect> = container(
        initialState = TopicPageState(),
        onCreate = {
            observeAuthState()
            observeTopicModel()
            observePagingData()
            viewModelScope.launch { visitTopicUseCase(topic) }
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

    private fun observeAuthState() = viewModelScope.launch {
        authStateUseCase().collectLatest { authState ->
            intent { reduce { state.copy(authState = authState) } }
        }
    }

    private fun observeTopicModel() = viewModelScope.launch {
        enrichTopicUseCase(topic).collectLatest { topicModel ->
            val topicState = TopicState.Topic(
                name = topicModel.topic.title,
                isFavorite = topicModel.isFavorite,
            )
            intent { reduce { state.copy(topicState = topicState) } }
        }
    }

    private fun observePagingData() = viewModelScope.launch {
        observeTopicPagingDataUseCase(
            id = topic.id,
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
        if (addCommentUseCase(topic.id, comment)) {
            pagingActions.refresh()
        } else {
            intent { postSideEffect(TopicSideEffect.ShowAddCommentError) }
        }
    }

    private fun onAddCommentClick() = intent {
        if (state.authState.isAuthorized) {
            postSideEffect(TopicSideEffect.ShowAddCommentDialog)
        } else {
            postSideEffect(TopicSideEffect.ShowLoginRequired)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(TopicSideEffect.Back)
    }

    private fun onFavoriteClick() = intent {
        enrichTopicUseCase(topic).take(1).collectLatest { toggleFavoriteUseCase(topic.id) }
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
