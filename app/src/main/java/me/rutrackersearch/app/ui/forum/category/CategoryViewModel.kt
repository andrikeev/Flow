package me.rutrackersearch.app.ui.forum.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireCategory
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.app.ui.paging.LoadStates
import me.rutrackersearch.domain.usecase.EnrichCategoriesUseCase
import me.rutrackersearch.domain.usecase.EnrichCategoryUseCase
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.LoadCategoryPageUseCase
import me.rutrackersearch.domain.usecase.UpdateBookmarkUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.domain.usecase.VisitCategoryUseCase
import me.rutrackersearch.models.forum.Category
import me.rutrackersearch.models.forum.CategoryModel
import me.rutrackersearch.models.forum.ForumItem
import me.rutrackersearch.models.search.Filter
import me.rutrackersearch.models.topic.Topic
import me.rutrackersearch.models.topic.TopicModel
import me.rutrackersearch.models.topic.Torrent
import me.rutrackersearch.utils.mapInstanceOf
import me.rutrackersearch.utils.newCancelableScope
import me.rutrackersearch.utils.relaunch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val enrichCategoriesUseCase: EnrichCategoriesUseCase,
    private val enrichCategoryUseCase: EnrichCategoryUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val loadCategoryPageUseCase: LoadCategoryPageUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val visitCategoryUseCase: VisitCategoryUseCase,
) : ViewModel(), ContainerHost<CategoryState, CategorySideEffect> {
    private val enrichScope = newCancelableScope()

    override val container: Container<CategoryState, CategorySideEffect> = container(
        initialState = CategoryState(CategoryModelState.Initial(savedStateHandle.requireCategory())),
        onCreate = { state ->
            viewModelScope.launch { visitCategoryUseCase(state.categoryModelState.category) }
            viewModelScope.launch {
                enrichCategoryUseCase(state.categoryModelState.category).collectLatest { categoryModel ->
                    val categoryModelState = CategoryModelState.Loaded(categoryModel)
                    intent { reduce { state.copy(categoryModelState = categoryModelState) } }
                }
            }
            loadFirstPage()
        },
    )

    fun perform(action: CategoryAction) = when (action) {
        is CategoryAction.BackClick -> onBackClick()
        is CategoryAction.BookmarkClick -> onBookmarkClick()
        is CategoryAction.CategoryClick -> onCategoryClick(action.category)
        is CategoryAction.EndOfListReached -> appendPage()
        is CategoryAction.FavoriteClick -> onFavoriteClick(action.topicModel)
        is CategoryAction.RetryClick -> loadFirstPage()
        is CategoryAction.SearchClick -> onSearchClick()
        is CategoryAction.TopicClick -> onTopicClick(action.topic)
        is CategoryAction.TorrentClick -> onTorrentClick(action.torrent)
    }

    private fun onBackClick() = intent { postSideEffect(CategorySideEffect.Back) }

    private fun onBookmarkClick() = intent {
        state.categoryModelState.let { categoryModelState ->
            if (categoryModelState is CategoryModelState.Loaded) {
                viewModelScope.launch { updateBookmarkUseCase(categoryModelState.categoryModel) }
            }
        }
    }

    private fun onCategoryClick(category: Category) = intent {
        postSideEffect(CategorySideEffect.OpenCategory(category))
    }

    private fun appendPage() = intent {
        val content = state.content
        val isNotLoading = state.loadStates.append != LoadState.Loading
        if (isNotLoading && content is CategoryContent.Content && content.page < content.pages) {
            reduce { state.copy(loadStates = LoadStates.append) }
            runCatching { loadPage(content.page + 1) }.onFailure { error ->
                reduce { state.copy(loadStates = LoadStates(append = LoadState.Error(error))) }
            }
        }
    }

    private fun onFavoriteClick(topicModel: TopicModel<out Topic>) = intent {
        viewModelScope.launch { updateFavoriteUseCase(topicModel) }
    }

    private fun loadFirstPage() = intent {
        reduce {
            state.copy(
                content = CategoryContent.Initial,
                loadStates = LoadStates.refresh,
            )
        }
        runCatching(::loadPage).onFailure { error ->
            reduce {
                state.copy(
                    content = CategoryContent.Initial,
                    loadStates = LoadStates(
                        refresh = LoadState.Error(error)
                    ),
                )
            }
        }
    }

    private fun onSearchClick() = intent {
        val filter = Filter(categories = listOf(state.categoryModelState.category))
        postSideEffect(CategorySideEffect.OpenSearch(filter))
    }

    private fun onTopicClick(topic: Topic) = intent {
        postSideEffect(CategorySideEffect.OpenTopic(topic))
    }

    private fun onTorrentClick(torrent: Torrent) = intent {
        postSideEffect(CategorySideEffect.OpenTorrent(torrent))
    }

    private fun loadPage(page: Int = 1) = intent {
        val id = state.categoryModelState.category.id
        val categoryPage = loadCategoryPageUseCase(id, page)
        enrichScope.relaunch {
            val categories = LinkedHashSet(state.content.getCategories())
                .plus(categoryPage.items.mapInstanceOf(ForumItem.Category::category))
                .toList()
            val topics = LinkedHashSet(state.content.getTopics())
                .plus(categoryPage.items.mapInstanceOf(ForumItem.Topic::topic))
                .toList()
            combine(
                enrichCategoriesUseCase(categories),
                enrichTopicsUseCase(topics),
            ) { categoryModels, topicModels ->
                if (categoryModels.isEmpty() && topicModels.isEmpty()) {
                    CategoryContent.Empty
                } else {
                    CategoryContent.Content(
                        categories = categoryModels,
                        topics = topicModels,
                        page = categoryPage.page,
                        pages = categoryPage.pages,
                    )
                }
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

    private fun CategoryContent.getCategories(): List<Category> = when (this) {
        is CategoryContent.Content -> categories.map(CategoryModel::category)
        is CategoryContent.Empty, is CategoryContent.Initial -> emptyList()
    }

    private fun CategoryContent.getTopics(): List<Topic> = when (this) {
        is CategoryContent.Content -> topics.map(TopicModel<out Topic>::topic)
        is CategoryContent.Empty, is CategoryContent.Initial -> emptyList()
    }
}
