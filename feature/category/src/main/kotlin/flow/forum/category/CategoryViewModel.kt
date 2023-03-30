package flow.forum.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import flow.common.launchCatching
import flow.common.newCancelableScope
import flow.common.relaunch
import flow.domain.usecase.*
import flow.models.forum.Category
import flow.models.forum.CategoryModel
import flow.models.search.Filter
import flow.models.topic.Topic
import flow.models.topic.TopicModel
import flow.models.topic.Torrent
import flow.ui.component.LoadState
import flow.ui.component.LoadStates
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
    private val enrichCategoriesUseCase: EnrichCategoriesUseCase,
    private val enrichCategoryUseCase: EnrichCategoryUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val getCategoryPageUseCase: GetCategoryPageUseCase,
    private val toggleBookmarkUseCase: ToggleBookmarkUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val visitCategoryUseCase: VisitCategoryUseCase,
) : ViewModel(), ContainerHost<CategoryState, CategorySideEffect> {
    private val enrichScope = viewModelScope.newCancelableScope()

    override val container: Container<CategoryState, CategorySideEffect> = container(
        initialState = CategoryState(CategoryModelState.Initial(savedStateHandle.category)),
        onCreate = {
            observeCategoryModel()
            loadFirstPage()
        },
    )

    fun perform(action: CategoryAction) = when (action) {
        is CategoryAction.BackClick -> onBackClick()
        is CategoryAction.BookmarkClick -> onBookmarkClick()
        is CategoryAction.CategoryClick -> onCategoryClick(action.category)
        is CategoryAction.EndOfListReached -> appendPage()
        is CategoryAction.FavoriteClick -> onFavoriteClick(action.topicModel)
        is CategoryAction.RetryClick -> onRetryClick()
        is CategoryAction.SearchClick -> onSearchClick()
        is CategoryAction.TopicClick -> onTopicClick(action.topic)
        is CategoryAction.TorrentClick -> onTorrentClick(action.torrent)
    }

    private fun observeCategoryModel() {
        intent {
            viewModelScope.launchCatching {
                enrichCategoryUseCase(state.categoryModelState.category)
                    .collectLatest { categoryModel ->
                        val categoryModelState = CategoryModelState.Loaded(categoryModel)
                        reduce { state.copy(categoryModelState = categoryModelState) }
                    }
            }
        }
    }

    private fun loadFirstPage() = intent {
        reduce {
            state.copy(
                content = CategoryContent.Initial,
                loadStates = LoadStates.refresh,
            )
        }
        viewModelScope.launchCatching(
            onFailure = { reduce { state.copy(loadStates = LoadStates(refresh = LoadState.Error(it))) } }
        ) {
            val id = state.categoryModelState.category.id
            val categoryPage = getCategoryPageUseCase(id, 1)
            visitCategoryUseCase(state.categoryModelState.category, categoryPage.items.topics())
            enrichScope.relaunch {
                val items = categoryPage.items
                if (items.isEmpty()) {
                    reduce {
                        state.copy(
                            content = CategoryContent.Empty,
                            loadStates = LoadStates(),
                        )
                    }
                } else {
                    val categories = items.categories()
                    val topics = items.topics()
                    combine(
                        enrichCategoriesUseCase(categories),
                        enrichTopicsUseCase(topics),
                    ) { categoryModels, topicModels ->
                        CategoryContent.Content(
                            categories = categoryModels,
                            topics = topicModels,
                            page = categoryPage.page,
                            pages = categoryPage.pages,
                        )
                    }.collectLatest { content ->
                        reduce {
                            state.copy(
                                content = content,
                                loadStates = LoadStates(),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onBackClick() {
        intent { postSideEffect(CategorySideEffect.Back) }
    }

    private fun onBookmarkClick() {
        intent {
            state.categoryModelState.let { categoryModelState ->
                if (categoryModelState is CategoryModelState.Loaded) {
                    viewModelScope.launch { toggleBookmarkUseCase(categoryModelState.categoryModel) }
                }
            }
        }
    }

    private fun onCategoryClick(category: Category) {
        intent { postSideEffect(CategorySideEffect.OpenCategory(category)) }
    }

    private fun appendPage() = intent {
        val content = state.content
        val isNotLoading = state.loadStates.append != LoadState.Loading
        if (isNotLoading && content is CategoryContent.Content && content.page < content.pages) {
            reduce { state.copy(loadStates = LoadStates.append) }
            viewModelScope.launchCatching(
                onFailure = { reduce { state.copy(loadStates = LoadStates(append = LoadState.Error(it))) } }
            ) {
                val id = state.categoryModelState.category.id
                val categoryPage = getCategoryPageUseCase(id, content.page + 1)
                enrichScope.relaunch {
                    val categories = LinkedHashSet(content.categories.map(CategoryModel::category))
                        .plus(categoryPage.items.categories())
                        .toList()
                    val topics = LinkedHashSet(content.topics.map(TopicModel<out Topic>::topic))
                        .plus(categoryPage.items.topics())
                        .toList()
                    combine(
                        enrichCategoriesUseCase(categories),
                        enrichTopicsUseCase(topics),
                    ) { categoryModels, topicModels ->
                        CategoryContent.Content(
                            categories = categoryModels,
                            topics = topicModels,
                            page = categoryPage.page,
                            pages = categoryPage.pages,
                        )
                    }.collectLatest { content ->
                        reduce {
                            state.copy(
                                content = content,
                                loadStates = LoadStates(),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) {
        intent { viewModelScope.launch { toggleFavoriteUseCase(topicModel) } }
    }

    private fun onRetryClick() {
        intent {
            if (state.loadStates.refresh is LoadState.Error) {
                loadFirstPage()
            } else if (state.loadStates.append is LoadState.Error) {
                appendPage()
            }
        }
    }

    private fun onSearchClick() = intent {
        val filter = Filter(categories = listOf(state.categoryModelState.category))
        postSideEffect(CategorySideEffect.OpenSearch(filter))
    }

    private fun onTopicClick(topic: Topic) = intent { postSideEffect(CategorySideEffect.OpenTopic(topic)) }

    private fun onTorrentClick(torrent: Torrent) = intent { postSideEffect(CategorySideEffect.OpenTorrent(torrent)) }
}
