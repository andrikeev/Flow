package flow.forum.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.domain.model.PagingAction
import flow.domain.model.append
import flow.domain.model.category.CategoryPage
import flow.domain.model.category.isEmpty
import flow.domain.model.retry
import flow.domain.usecase.ObserveAuthStateUseCase
import flow.domain.usecase.ObserveCategoryModelUseCase
import flow.domain.usecase.ObserveCategoryPagingDataUseCase
import flow.domain.usecase.ToggleBookmarkUseCase
import flow.domain.usecase.ToggleFavoriteUseCase
import flow.logger.api.LoggerFactory
import flow.models.auth.AuthState
import flow.models.forum.Category
import flow.models.topic.Topic
import flow.models.topic.TopicModel
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
internal class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    loggerFactory: LoggerFactory,
    private val authStateUseCase: ObserveAuthStateUseCase,
    private val observeCategoryPagingDataUseCase: ObserveCategoryPagingDataUseCase,
    private val observeCategoryModelUseCase: ObserveCategoryModelUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel(), ContainerHost<CategoryPageState, CategorySideEffect> {
    private val logger = loggerFactory.get("CategoryViewModel")
    private val categoryId = savedStateHandle.categoryId
    private val pagingActions = MutableSharedFlow<PagingAction>()

    override val container: Container<CategoryPageState, CategorySideEffect> = container(
        initialState = CategoryPageState(),
        onCreate = {
            observeAuthState()
            observeCategoryModel()
            observePagingData()
        },
    )

    fun perform(action: CategoryAction) {
        logger.d { "Perform $action" }
        when (action) {
            is CategoryAction.BackClick -> onBackClick()
            is CategoryAction.BookmarkClick -> onBookmarkClick()
            is CategoryAction.CategoryClick -> onCategoryClick(action.category)
            is CategoryAction.EndOfListReached -> appendPage()
            is CategoryAction.FavoriteClick -> onFavoriteClick(action.topicModel)
            is CategoryAction.LoginClick -> onLoginClick()
            is CategoryAction.RetryClick -> onRetryClick()
            is CategoryAction.SearchClick -> onSearchClick()
            is CategoryAction.TopicClick -> onTopicClick(action.topicModel)
        }
    }

    private fun observeAuthState() = intent {
        logger.d { "Start observing auth state" }
        authStateUseCase().collectLatest { authState ->
            reduce { state.copy(authState = authState) }
        }
    }

    private fun observeCategoryModel() = intent {
        logger.d { "Start observing category model" }
        observeCategoryModelUseCase(categoryId).collectLatest { categoryModel ->
            val categoryState = CategoryState.Category(
                name = categoryModel.category.name,
                isBookmark = categoryModel.isBookmark,
            )
            reduce { state.copy(categoryState = categoryState) }
        }
    }

    private fun observePagingData() = intent {
        logger.d { "Start observing paging data" }
        observeCategoryPagingDataUseCase(
            id = categoryId,
            actionsFlow = pagingActions,
            scope = viewModelScope,
        ).collectLatest { (data, loadStates) ->
            reduce {
                state.copy(
                    categoryContent = when {
                        data == null -> CategoryContent.Initial
                        data.isEmpty() -> CategoryContent.Empty
                        else -> data.toContent()
                    },
                    loadStates = loadStates,
                )
            }
        }
    }

    private fun onBackClick() = intent {
        postSideEffect(CategorySideEffect.Back)
    }

    private fun onBookmarkClick() = intent {
        toggleBookmarkUseCase(categoryId)
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(CategorySideEffect.OpenCategory(category.id))
    }

    private fun appendPage() = intent {
        pagingActions.append()
    }

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) = intent {
        runCatching { toggleFavoriteUseCase(topicModel.topic.id) }
            .onFailure { postSideEffect(CategorySideEffect.ShowFavoriteToggleError) }
    }

    private fun onLoginClick() = intent {
        postSideEffect(CategorySideEffect.OpenLogin)
    }

    private fun onRetryClick() = intent {
        pagingActions.retry()
    }

    private fun onSearchClick() = intent {
        when (state.authState) {
            is AuthState.Authorized -> postSideEffect(CategorySideEffect.OpenSearch(categoryId))
            is AuthState.Unauthorized -> postSideEffect(CategorySideEffect.ShowLoginDialog)
        }
    }

    private fun onTopicClick(topicModel: TopicModel<out Topic>) = intent {
        postSideEffect(CategorySideEffect.OpenTopic(topicModel.topic.id))
    }

    private fun CategoryPage.toContent(): CategoryContent.Content {
        return CategoryContent.Content(
            items = buildList {
                categories.forEach { add(CategoryItem.Category(it)) }
                topics
                    .groupBy { model ->
                        sections.firstOrNull { it.topics.contains(model.topic.id) }
                    }
                    .forEach { (section, topics) ->
                        if (section != null) {
                            add(CategoryItem.SectionHeader(section.name))
                        }
                        topics.forEach { add(CategoryItem.Topic(it)) }
                    }
            },
        )
    }
}
