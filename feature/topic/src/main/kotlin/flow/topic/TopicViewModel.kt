package flow.topic

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
import flow.domain.usecase.DownloadTorrentUseCase
import flow.domain.usecase.GetTopicUseCase
import flow.domain.usecase.IsAuthorizedUseCase
import flow.domain.usecase.ObserveFavoriteStateUseCase
import flow.domain.usecase.ObserveTopicPagingDataUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.forum.Category
import flow.models.search.Filter
import flow.models.topic.Author
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val addCommentUseCase: AddCommentUseCase,
    private val downloadTorrentUseCase: DownloadTorrentUseCase,
    private val getTopicUseCase: GetTopicUseCase,
    private val isAuthorizedUseCase: IsAuthorizedUseCase,
    private val observeFavoriteStateUseCase: ObserveFavoriteStateUseCase,
    private val observeTopicPagingDataUseCase: ObserveTopicPagingDataUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<TopicState, TopicSideEffect> {
    private val logger = loggerFactory.get("OpenTopicViewModel")
    private val id = savedStateHandle.id
    private val pagingActions = MutableSharedFlow<PagingAction>()

    override val container: Container<TopicState, TopicSideEffect> = container(
        initialState = TopicState(),
        onCreate = {
            loadTopic()
            observePagingData()
            observeFavoritesState()
        },
    )

    fun perform(action: TopicAction): Any {
        return when (action) {
            is TopicAction.AddComment -> onAddComment(action.comment)
            is TopicAction.AddCommentClick -> onAddCommentClick()
            is TopicAction.AuthorClick -> onAuthorClick(action.author)
            is TopicAction.BackClick -> onBackClick()
            is TopicAction.CategoryClick -> onCategoryClick(action.category)
            is TopicAction.FavoriteClick -> onFavoriteClick()
            is TopicAction.GoToPage -> onGoToPage(action.page)
            is TopicAction.LastVisibleIndexChanged -> onLastVisibleIndexChanged(action.index)
            is TopicAction.ListBottomReached -> onListBottomReached()
            is TopicAction.ListTopReached -> onListTopReached()
            is TopicAction.LoginClick -> onLoginClick()
            is TopicAction.MagnetClick -> onMagnetClick(action.link)
            is TopicAction.OpenFileClick -> onOpenFileClick(action.uri)
            is TopicAction.ShareClick -> onShareClick()
            is TopicAction.RetryClick -> onRetryClick()
            is TopicAction.TorrentFileClick -> onTorrentFileClick(action.title)
        }
    }

    private fun loadTopic() = intent {
        runCatching { coroutineScope { getTopicUseCase(id) } }
            .onSuccess { topic ->
                reduce {
                    val torrentData = topic.torrentData
                    state.copy(
                        topicContent = if (torrentData != null) {
                            TopicContent.Torrent(
                                title = topic.title,
                                data = torrentData,
                            )
                        } else {
                            TopicContent.Topic(topic.title)
                        }
                    )
                }
            }
            .onFailure { }
    }

    private fun observeFavoritesState() = intent {
        observeFavoriteStateUseCase(id).collectLatest { isFavorite ->
            val favoriteState = TopicFavoriteState.FavoriteState(isFavorite)
            reduce { state.copy(favoriteState = favoriteState) }
        }
    }

    private fun observePagingData() = intent {
        observeTopicPagingDataUseCase(
            id = id,
            actions = pagingActions,
            scope = viewModelScope,
        ).collectLatest { (data, loadStates, pagination) ->
            reduce {
                state.copy(
                    paginationState = when (val paginationState = state.paginationState) {
                        is PaginationState.Initial,
                        is PaginationState.NoPagination -> if (pagination.totalPages > 1) {
                            PaginationState.Pagination(
                                page = pagination.loadedPages.first,
                                loadedPages = pagination.loadedPages,
                                totalPages = pagination.totalPages,
                            )
                        } else {
                            PaginationState.NoPagination
                        }

                        is PaginationState.Pagination -> if (pagination.totalPages > 1) {
                            paginationState.copy(
                                loadedPages = pagination.loadedPages,
                                totalPages = pagination.totalPages,
                            )
                        } else {
                            PaginationState.NoPagination
                        }
                    },
                    commentsContent = when {
                        data == null -> CommentsContent.Initial
                        data.isEmpty() -> CommentsContent.Empty
                        else -> CommentsContent.Posts(data)
                    },
                    loadStates = loadStates,
                )
            }
        }
    }

    private fun onAddComment(comment: String) = intent {
        if (addCommentUseCase(id, comment)) {
            pagingActions.refresh()
        } else {
            postSideEffect(TopicSideEffect.ShowAddCommentError)
        }
    }

    private fun onAddCommentClick() = intent {
        if (isAuthorizedUseCase()) {
            postSideEffect(TopicSideEffect.ShowAddCommentDialog)
        } else {
            postSideEffect(TopicSideEffect.ShowLoginRequired)
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(TopicSideEffect.Back)
    }

    private fun onFavoriteClick() = intent {
        toggleFavoriteUseCase(id)
    }

    private fun onLastVisibleIndexChanged(index: Int) = intent {
        when (val paginationState = state.paginationState) {
            is PaginationState.Initial -> Unit
            is PaginationState.NoPagination -> Unit
            is PaginationState.Pagination -> reduce {
                state.copy(
                    paginationState = paginationState.copy(
                        page = paginationState.loadedPages.first + (index + 1) / PageSize,
                    )
                )
            }
        }
    }

    private fun onGoToPage(page: Int) = intent {
        pagingActions.refresh(page)
    }

    private fun onListBottomReached() = intent {
        pagingActions.append()
    }

    private fun onListTopReached() = intent {
        pagingActions.prepend()
    }

    private fun onShareClick() = intent {
        val link = createShareLink()
        postSideEffect(TopicSideEffect.ShareLink(link))
    }

    private fun onRetryClick() = intent {
        pagingActions.retry()
    }

    private fun onLoginClick() = intent {
        postSideEffect(TopicSideEffect.OpenLogin)
    }

    private fun onAuthorClick(author: Author) = intent {
        postSideEffect(TopicSideEffect.OpenSearch(Filter(author = author)))
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(TopicSideEffect.OpenCategory(category.id))
    }

    private fun onMagnetClick(link: String) = intent {
        postSideEffect(TopicSideEffect.ShowMagnet(link))
    }

    private fun onTorrentFileClick(title: String) = intent {
        if (isAuthorizedUseCase()) {
            postSideEffect(TopicSideEffect.ShowDownloadProgress)
            reduce { state.copy(downloadState = DownloadState.Started) }
            val uri = downloadTorrentUseCase(id, title)
            if (uri != null) {
                intent { reduce { state.copy(downloadState = DownloadState.Completed(uri)) } }
            } else {
                intent { reduce { state.copy(downloadState = DownloadState.Error) } }
            }
        } else {
            intent { postSideEffect(TopicSideEffect.ShowLoginRequired) }
        }
    }

    private fun onOpenFileClick(uri: String) = intent {
        postSideEffect(TopicSideEffect.OpenFile(uri))
    }

    private fun createShareLink(): String {
        return "https://rutracker.org/forum/viewtopic.php?t=$id"
    }

    private companion object {
        const val PageSize = 30
    }
}
