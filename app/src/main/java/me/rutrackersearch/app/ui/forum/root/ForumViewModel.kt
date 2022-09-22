package me.rutrackersearch.app.ui.forum.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import me.rutrackersearch.domain.usecase.LoadForumUseCase
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.RootCategory
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ForumViewModel @Inject constructor(
    private val loadForumUseCase: LoadForumUseCase,
) : ViewModel(), ContainerHost<ForumState, ForumSideEffect> {
    override val container: Container<ForumState, ForumSideEffect> = container(
        initialState = ForumState.Loading,
        onCreate = { loadForum() },
    )

    fun perform(action: ForumAction) {
        when (action) {
            is ForumAction.CategoryClick -> onCategoryClick(action.category)
            is ForumAction.ExpandClick -> onExpandClick(action.expandable)
            is ForumAction.RetryClick -> loadForum()
        }
    }

    private fun loadForum() {
        intent { reduce { ForumState.Loading } }
        viewModelScope.launch {
            runCatching { loadForumUseCase() }
                .onSuccess { intent { reduce { ForumState.Loaded(it.children.map(::Expandable)) } } }
                .onFailure { intent { reduce { ForumState.Error(it) } } }
        }
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(ForumSideEffect.OpenCategory(category))
    }

    private fun onExpandClick(value: Expandable<RootCategory>) = intent {
        (state as? ForumState.Loaded)?.let { state ->
            reduce {
                state.copy(
                    forum = state.forum.map { expandable ->
                        if (expandable.item.name == value.item.name) {
                            expandable.copy(expanded = !expandable.expanded)
                        } else {
                            expandable
                        }
                    },
                )
            }
        }
    }
}
