package me.rutrackersearch.app.ui.topic.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireTopic
import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.app.ui.paging.PagingAction
import me.rutrackersearch.app.ui.paging.PagingData
import me.rutrackersearch.app.ui.paging.PagingDataLoader
import me.rutrackersearch.app.ui.topic.topic.TopicAction.AddComment
import me.rutrackersearch.app.ui.topic.topic.TopicAction.EndOfListReached
import me.rutrackersearch.app.ui.topic.topic.TopicAction.FirstVisibleItemIndexChanged
import me.rutrackersearch.app.ui.topic.topic.TopicAction.ListTopReached
import me.rutrackersearch.app.ui.topic.topic.TopicAction.RetryClick
import me.rutrackersearch.domain.usecase.AddCommentUseCase
import me.rutrackersearch.domain.usecase.LoadTopicPageUseCase
import me.rutrackersearch.domain.usecase.VisitTopicUseCase
import me.rutrackersearch.models.topic.Post
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.utils.newCancelableScope
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadTopicPageUseCase: LoadTopicPageUseCase,
    private val visitTopicUseCase: VisitTopicUseCase,
    private val addCommentUseCase: AddCommentUseCase,
) : ViewModel() {
    private val topic = savedStateHandle.requireTopic()
    private val mutablePagingActions = MutableSharedFlow<PagingAction>(1)
    private val mutableFirstVisibleIndex = MutableStateFlow(0)
    private val mutablePagingData = MutableSharedFlow<PagingData<Post>>(1)
    private val mutablePageNumber = MutableStateFlow(0)
    private val mutablePagesCount = MutableStateFlow(0)
    private val mutableFirstLoadedPageNumber = MutableStateFlow(0)

    private var pagingDataScope: CoroutineScope? = null

    val state: StateFlow<TopicState> = combine(
        mutablePagingData,
        mutablePageNumber,
        mutablePagesCount,
    ) { pagingData, page, pages ->
        TopicState(
            topic = TopicModel(topic),
            page = page,
            pages = pages,
            content = when (val loadState = pagingData.loadStates.refresh) {
                is LoadState.Loading -> PageResult.Loading()
                is LoadState.Error -> PageResult.Error(loadState.error)
                is LoadState.NotLoading -> {
                    val posts = pagingData.items
                    if (posts.isEmpty()) {
                        PageResult.Empty()
                    } else {
                        PageResult.Content(
                            content = posts,
                            prepend = pagingData.loadStates.prepend,
                            append = pagingData.loadStates.append,
                        )
                    }
                }
            }
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, TopicState(TopicModel(topic)))

    init {
        reloadFromPage()
        viewModelScope.launch {
            visitTopicUseCase(topic)
            combine(
                mutableFirstLoadedPageNumber,
                mutableFirstVisibleIndex,
            ) { firstLoadedPage, firstVisibleIndex ->
                firstLoadedPage + ((firstVisibleIndex + 1) / PageSize)
            }.collectLatest(mutablePageNumber::emit)
        }
    }

    fun perform(action: TopicAction) {
        when (action) {
            EndOfListReached -> viewModelScope.launch {
                mutablePagingActions.emit(PagingAction.Append)
            }
            ListTopReached -> viewModelScope.launch {
                mutablePagingActions.emit(PagingAction.Prepend)
            }
            RetryClick -> viewModelScope.launch {
                mutablePagingActions.emit(PagingAction.Retry)
            }
            is TopicAction.GoToPage -> viewModelScope.launch {
                reloadFromPage(action.page)
            }
            is AddComment -> viewModelScope.launch {
                runCatching { addCommentUseCase(topic.id, action.comment) }
                    .onSuccess { mutablePagingActions.emit(PagingAction.Refresh) }
            }
            is FirstVisibleItemIndexChanged -> viewModelScope.launch {
                mutableFirstVisibleIndex.emit(action.index)
            }
            else -> Unit
        }
    }

    private fun reloadFromPage(initialPage: Int = 1) {
        pagingDataScope?.cancel()
        pagingDataScope = newCancelableScope()
        pagingDataScope?.let { scope ->
            scope.launch {
                mutableFirstLoadedPageNumber.emit(initialPage)
                mutableFirstVisibleIndex.emit(0)
                PagingDataLoader(
                    initialPage = initialPage,
                    pageSize = PageSize,
                    fetchData = { page ->
                        loadTopicPageUseCase(topic.id, page).also { loadedPage ->
                            mutableFirstLoadedPageNumber.emit(
                                if (mutableFirstLoadedPageNumber.value == 0) {
                                    loadedPage.page
                                } else {
                                    minOf(mutableFirstLoadedPageNumber.value, loadedPage.page)
                                }
                            )
                            mutablePagesCount.emit(loadedPage.pages)
                        }
                    },
                    actions = mutablePagingActions,
                    scope = this,
                )
                    .flow
                    .collect(mutablePagingData::emit)
            }
        }
    }

    companion object {
        private const val PageSize = 30
    }
}
