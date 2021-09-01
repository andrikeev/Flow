package me.rutrackersearch.app.ui.forum.forumtree

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
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeAction.ExpandClick
import me.rutrackersearch.app.ui.forum.forumtree.ForumTreeAction.RetryClick
import me.rutrackersearch.domain.entity.forum.ForumTree
import me.rutrackersearch.domain.usecase.LoadForumTreeUseCase
import javax.inject.Inject

@HiltViewModel
class ForumTreeViewModel @Inject constructor(
    private val loadForumTreeUseCase: LoadForumTreeUseCase,
) : ViewModel() {
    private val mutableForumTreeLoading = MutableStateFlow<Result<ForumTree>>(Result.Loading())
    private val mutableExpandedSet = MutableStateFlow<Set<String>>(emptySet())

    val state: StateFlow<ForumTreeState> = combine(
        mutableForumTreeLoading,
        mutableExpandedSet,
    ) { loadingState, expandedSet ->
        when (loadingState) {
            is Result.Loading -> ForumTreeState.Loading
            is Result.Content -> ForumTreeState.Loaded(
                loadingState.content.mapToViewState(expandedSet)
            )
            is Result.Error -> ForumTreeState.Error(loadingState.error)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ForumTreeState.Loading)

    init {
        viewModelScope.launch {
            loadForum()
        }
    }

    fun perform(treeAction: ForumTreeAction) {
        viewModelScope.launch {
            when (treeAction) {
                is RetryClick -> loadForum()
                is ExpandClick -> {
                    val currentSet = mutableExpandedSet.value
                    mutableExpandedSet.emit(
                        if (treeAction.expandable.expanded) {
                            currentSet.minus(treeAction.expandable.id)
                        } else {
                            currentSet.plus(treeAction.expandable.id)
                        }
                    )
                }
                else -> Unit
            }
        }
    }

    private suspend fun loadForum() {
        mutableForumTreeLoading.emit(Result.Loading())
        kotlin.runCatching { loadForumTreeUseCase.invoke() }
            .onSuccess { mutableForumTreeLoading.emit(Result.Content(it)) }
            .onFailure { mutableForumTreeLoading.emit(Result.Error(it)) }
    }

    private fun ForumTree.mapToViewState(expandedSet: Set<String>): List<ForumTreeItem> {
        return children.mapIndexed { index, forumTreeRootGroup ->
            val forumTreeRootId = "c-$index"
            val isForumTreeRootExpanded = expandedSet.contains(forumTreeRootId)
            listOf<ForumTreeItem>(
                ExpandableForumTreeRootGroup(
                    id = forumTreeRootId,
                    name = forumTreeRootGroup.name,
                    expanded = isForumTreeRootExpanded,
                )
            ) + if (isForumTreeRootExpanded) {
                forumTreeRootGroup.children.map { forumTreeGroup ->
                    val forumTreeGroupId = forumTreeGroup.category.id
                    val isForumTreeGroupExpanded =
                        expandedSet.contains(forumTreeGroupId)
                    listOf(
                        ExpandableForumTreeGroup(
                            id = forumTreeGroupId,
                            name = forumTreeGroup.category.name,
                            expandable = forumTreeGroup.children.isNotEmpty(),
                            expanded = isForumTreeGroupExpanded,
                        )
                    ) + if (isForumTreeGroupExpanded) {
                        forumTreeGroup.children.map { category ->
                            ForumTreeCategory(
                                id = category.id,
                                name = category.name,
                            )
                        }
                    } else {
                        emptyList()
                    }
                }.flatten()
            } else {
                emptyList()
            }
        }.flatten()
    }
}
