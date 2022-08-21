package me.rutrackersearch.app.ui.forum.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.common.Result
import me.rutrackersearch.domain.usecase.LoadForumUseCase
import me.rutrackersearch.models.forum.Forum
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val loadForumUseCase: LoadForumUseCase,
) : ViewModel() {
    private val mutableForumLoading = MutableStateFlow<Result<Forum>>(Result.Loading())
    private val mutableExpandedSet = MutableStateFlow<Set<String>>(emptySet())

    val state: StateFlow<ForumState> = combine(
        mutableForumLoading,
        mutableExpandedSet,
    ) { loadingState, expandedSet ->
        when (loadingState) {
            is Result.Loading -> ForumState.Loading
            is Result.Content -> {
                ForumState.Loaded(
                    loadingState.content.children.map { item ->
                        Expandable(item, expandedSet.contains(item.name))
                    }
                )
            }
            is Result.Error -> ForumState.Error(loadingState.error)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ForumState.Loading)

    init {
        viewModelScope.launch {
            loadForum()
        }
    }

    fun perform(action: ForumAction) {
        viewModelScope.launch {
            when (action) {
                is ForumAction.RetryClick -> loadForum()
                is ForumAction.ExpandClick -> {
                    val currentSet = mutableExpandedSet.value
                    mutableExpandedSet.emit(
                        if (action.expandable.expanded) {
                            currentSet.minus(action.expandable.item.name)
                        } else {
                            currentSet.plus(action.expandable.item.name)
                        }
                    )
                }
                else -> Unit
            }
        }
    }

    private suspend fun loadForum() {
        mutableForumLoading.emit(Result.Loading())
        kotlin.runCatching { loadForumUseCase.invoke() }
            .onSuccess { mutableForumLoading.emit(Result.Content(it)) }
            .onFailure { mutableForumLoading.emit(Result.Error(it)) }
    }
}
