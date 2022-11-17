package flow.forum.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.common.launchCatching
import flow.domain.usecase.LoadForumUseCase
import flow.models.forum.Category
import flow.models.forum.RootCategory
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class RootForumViewModel @Inject constructor(
    private val loadForumUseCase: LoadForumUseCase,
) : ViewModel(), ContainerHost<RootForumState, RootForumSideEffect> {
    override val container: Container<RootForumState, RootForumSideEffect> = container(
        initialState = RootForumState.Loading,
        onCreate = { loadForum() },
    )

    fun perform(action: RootForumAction) {
        when (action) {
            is RootForumAction.CategoryClick -> onCategoryClick(action.category)
            is RootForumAction.ExpandClick -> onExpandClick(action.expandable)
            is RootForumAction.RetryClick -> loadForum()
        }
    }

    private fun loadForum() = intent {
        reduce { RootForumState.Loading }
        viewModelScope.launchCatching(
            onFailure = { reduce { RootForumState.Error(it) } }
        ) {
            val forum = loadForumUseCase()
            reduce { RootForumState.Loaded(forum.children.map(::Expandable)) }
        }
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(RootForumSideEffect.OpenCategory(category))
    }

    private fun onExpandClick(value: Expandable<RootCategory>) = intent {
        (state as? RootForumState.Loaded)?.let { state ->
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
