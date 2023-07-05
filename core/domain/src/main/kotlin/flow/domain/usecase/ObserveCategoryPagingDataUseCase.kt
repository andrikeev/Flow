package flow.domain.usecase

import flow.data.api.service.ForumService
import flow.domain.model.PagingAction
import flow.domain.model.PagingData
import flow.domain.model.PagingDataLoader
import flow.domain.model.category.CategoryPage
import flow.domain.model.refresh
import flow.logger.api.LoggerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class ObserveCategoryPagingDataUseCase @Inject constructor(
    private val forumService: ForumService,
    private val enrichTopicsUseCase: EnrichTopicsUseCase,
    private val visitCategoryUseCase: VisitCategoryUseCase,
    private val loggerFactory: LoggerFactory,
) {
    suspend operator fun invoke(
        id: String,
        actionsFlow: Flow<PagingAction>,
        scope: CoroutineScope,
    ): Flow<PagingData<CategoryPage>> {
        return PagingDataLoader(
            fetchData = { page ->
                forumService.getCategoryPage(id, page).also { categoryPage ->
                    if (categoryPage.page == 1) {
                        visitCategoryUseCase(id, categoryPage.items.topics())
                    }
                }
            },
            transform = { forumItems ->
                enrichTopicsUseCase(forumItems.topics()).map { topicModels ->
                    CategoryPage(
                        categories = forumItems.categories(),
                        topics = topicModels,
                    )
                }
            },
            actions = actionsFlow.onStart { refresh() },
            scope = scope,
            logger = loggerFactory.get("CategoryPagingDataLoader"),
        ).flow
    }
}
