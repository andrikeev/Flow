package flow.forum

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.usecase.GetForumUseCase
import flow.logger.api.LoggerFactory
import flow.models.forum.ForumCategory
import kotlinx.coroutines.coroutineScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class ForumViewModel @Inject constructor(
    private val getForumUseCase: GetForumUseCase,
    loggerFactory: LoggerFactory,
) : ViewModel(), ContainerHost<ForumState, ForumSideEffect> {
    private val logger = loggerFactory.get("ForumViewModel")

    override val container: Container<ForumState, ForumSideEffect> = container(
        initialState = ForumState.Loading,
        onCreate = { loadForum() },
    )

    fun perform(action: ForumAction) {
        logger.d { "Perform $action" }
        when (action) {
            is ForumAction.CategoryClick -> onCategoryClick(action.category)
            is ForumAction.ExpandClick -> onExpandClick(action.expandable)
            is ForumAction.RetryClick -> loadForum()
        }
    }

    private fun loadForum() = intent {
        logger.d { "Launch load forum" }
        reduce { ForumState.Loading }
        runCatching { coroutineScope { getForumUseCase() } }
            .onSuccess { forum ->
                logger.d { "Forum loaded" }
                reduce { ForumState.Loaded(forum.children.map(::Expandable)) }
            }
            .onFailure { error ->
                logger.e(error) { "Forum load error" }
                reduce { ForumState.Error(error) }
            }
    }

    private fun onCategoryClick(category: ForumCategory) = intent {
        postSideEffect(ForumSideEffect.OpenCategory(category.id))
    }

    private fun onExpandClick(value: Expandable<ForumCategory>) = intent {
        reduce {
            when (val state = state) {
                is ForumState.Error -> state
                is ForumState.Loaded -> state.copy(
                    forum = state.forum.map { expandable ->
                        if (expandable.item == value.item) {
                            expandable.copy(expanded = !expandable.expanded)
                        } else {
                            expandable
                        }
                    }
                )
                is ForumState.Loading -> state
            }
        }
    }
}
