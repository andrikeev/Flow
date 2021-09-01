package me.rutrackersearch.app.ui.forum.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.rutrackersearch.app.ui.args.requireCategory
import me.rutrackersearch.app.ui.common.PageResult
import me.rutrackersearch.app.ui.forum.category.CategoryAction.BookmarkClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.EndOfListReached
import me.rutrackersearch.app.ui.forum.category.CategoryAction.FavoriteClick
import me.rutrackersearch.app.ui.forum.category.CategoryAction.RetryClick
import me.rutrackersearch.app.ui.paging.LoadState
import me.rutrackersearch.app.ui.paging.PagingAction
import me.rutrackersearch.app.ui.paging.PagingDataLoader
import me.rutrackersearch.domain.entity.CategoryModel
import me.rutrackersearch.domain.entity.forum.ForumCategory
import me.rutrackersearch.domain.entity.forum.ForumTopic
import me.rutrackersearch.domain.usecase.EnrichCategoriesUseCase
import me.rutrackersearch.domain.usecase.EnrichCategoryUseCase
import me.rutrackersearch.domain.usecase.EnrichTopicsUseCase
import me.rutrackersearch.domain.usecase.LoadCategoryPageUseCase
import me.rutrackersearch.domain.usecase.UpdateBookmarkUseCase
import me.rutrackersearch.domain.usecase.UpdateFavoriteUseCase
import me.rutrackersearch.domain.usecase.VisitCategoryUseCase
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadCategoryPageUseCase: LoadCategoryPageUseCase,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val enrichCategoriesUseCase: EnrichCategoriesUseCase,
    private val enrichCategoryUseCase: EnrichCategoryUseCase,
    private val updateBookmarkUseCase: UpdateBookmarkUseCase,
    private val visitCategoryUseCase: VisitCategoryUseCase,
) : ViewModel() {

    private val category = savedStateHandle.requireCategory()
    private val mutablePagingActions = MutableSharedFlow<PagingAction>(1) //TODO: do not replay

    private val pagingData = PagingDataLoader(
        pageSize = 50,
        fetchData = { page -> loadCategoryPageUseCase(category.id, page) },
        actions = mutablePagingActions,
        scope = viewModelScope,
    ).flow

    val state: StateFlow<CategoryState> = pagingData
        .flatMapLatest { pagingData ->
            val categories = pagingData.items
                .filterIsInstance<ForumCategory>()
                .map(ForumCategory::category)
            val topics = pagingData.items
                .filterIsInstance<ForumTopic>()
                .map(ForumTopic::topic)
            combine(
                enrichCategoryUseCase(category),
                enrichCategoriesUseCase(categories),
                enrichTopicsUseCase(topics),
            ) { categoryModel, categoryModels, topicModels ->
                CategoryState(
                    category = categoryModel,
                    content = when (val loadState = pagingData.loadStates.refresh) {
                        is LoadState.Loading -> PageResult.Loading()
                        is LoadState.Error -> PageResult.Error(loadState.error)
                        is LoadState.NotLoading -> {
                            if (categoryModels.isNotEmpty() || topicModels.isNotEmpty()) {
                                PageResult.Content(
                                    content = CategoryContent(
                                        categories = categoryModels,
                                        topics = topicModels,
                                    ),
                                    prepend = pagingData.loadStates.prepend,
                                    append = pagingData.loadStates.append,
                                )
                            } else {
                                PageResult.Empty()
                            }
                        }
                    }
                )
            }
        }
        .onStart { visitCategoryUseCase(category) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = CategoryState(CategoryModel(category)),
        )

    fun perform(action: CategoryAction) {
        viewModelScope.launch {
            when (action) {
                is RetryClick -> mutablePagingActions.emit(PagingAction.Retry)
                is FavoriteClick -> updateFavoriteUseCase(action.topicModel)
                is BookmarkClick -> updateBookmarkUseCase(action.category)
                is EndOfListReached -> mutablePagingActions.emit(PagingAction.Append)
                else -> Unit
            }
        }
    }
}
